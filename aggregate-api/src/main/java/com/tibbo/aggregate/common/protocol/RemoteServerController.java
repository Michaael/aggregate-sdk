package com.tibbo.aggregate.common.protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;

import com.tibbo.aggregate.common.AggreGateException;
import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.data.Data;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.device.RemoteDeviceErrorException;
import com.tibbo.aggregate.common.security.TokenProvider;
import com.tibbo.aggregate.common.server.CommonServerFormats;
import com.tibbo.aggregate.common.server.RootContextConstants;
import com.tibbo.aggregate.common.util.BlockingChannel;
import com.tibbo.aggregate.common.util.SocketBlockingChannel;

public class RemoteServerController extends AbstractAggreGateDeviceController<RemoteServer, RemoteContextManager>
{
  private static final String CHECK_CONNECTION_TIMER_PREFIX = "checkConnectionTimer/";
  private Timer connectionTimer;
  private BlockingChannel dataChannel;
  
  public RemoteServerController(RemoteServer device, boolean async)
  {
    this(device, async, false);
  }
  
  public RemoteServerController(RemoteServer device, boolean async, boolean json)
  {
    this(device, async, true, Log.COMMANDS_CLIENT, Integer.MAX_VALUE, json);
  }
  
  public RemoteServerController(RemoteServer device, boolean async, boolean useContextManager, Logger logger, int maxEventQueueLength)
  {
    this(device, async, useContextManager, logger, maxEventQueueLength, false);
  }
  
  public RemoteServerController(RemoteServer device, boolean async, boolean useContextManager, Logger logger, int maxEventQueueLength, boolean json)
  {
    super(device, logger, maxEventQueueLength, json);
    if (useContextManager)
    {
      setContextManager(new RemoteContextManager(this, async, maxEventQueueLength));
    }
  }
  
  @Override
  protected boolean connectImpl() throws InterruptedException, DisconnectionException, IOException, RemoteDeviceErrorException, ContextException
  {
    prepareDataChannel();
    super.connectImpl();
    
    if (getContextManager() != null)
    {
      getContextManager().setRoot(new ProxyContext(Contexts.CTX_ROOT, this));
      getContextManager().restart();
    }
    
    return true;
  }
  
  protected void prepareDataChannel() throws RemoteDeviceErrorException
  {
    try
    {
      if (dataChannel == null && getDevice().getAddress() != null)
      {
        Log.PROTOCOL.debug("Connecting to remote server (" + getDevice() + ")");
        
        // Determine if we should use secure connection based on port
        // Port 6461 is the default non-secure port, all others use SSL
        boolean useSecureConnection = getDevice().getPort() != RemoteServer.DEFAULT_NON_SECURE_PORT;
        
        Socket socket;
        int timeout = new Long(getDevice().getConnectionTimeout()).intValue();
        
        try
        {
          if (useSecureConnection)
          {
            // Use SSL connection for secure port (default: 6460)
            SSLSocketFactory sslFactory = getTrustedSocketFactory(getDevice());
            SSLSocket sslSocket = (SSLSocket) sslFactory.createSocket();
            sslSocket.setSoTimeout(timeout);
            sslSocket.connect(new InetSocketAddress(getDevice().getAddress(), getDevice().getPort()), timeout);
            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
            sslSocket.startHandshake();
            socket = sslSocket;
            Log.PROTOCOL.debug("Using SSL connection to " + getDevice().getAddress() + ":" + getDevice().getPort());
          }
          else
          {
            // Use plain socket for non-secure port (default: 6461)
            socket = SocketFactory.getDefault().createSocket();
            socket.setSoTimeout(timeout);
            socket.connect(new InetSocketAddress(getDevice().getAddress(), getDevice().getPort()), timeout);
            Log.PROTOCOL.debug("Using non-secure connection to " + getDevice().getAddress() + ":" + getDevice().getPort());
          }
        }
        catch (IOException ex)
        {
          String msg = getConnectionErrorMessage();
          if (msg == null)
          {
            throw ex;
          }
          else
          {
            throw new RemoteDeviceErrorException(msg, ex);
          }
        }
        
        dataChannel = new SocketBlockingChannel(socket);
      }
      
      if (dataChannel != null)
      {
        setCommandParser(new AggreGateCommandParser(dataChannel));
      }
      
      Log.PROTOCOL.debug("Connection with remote server established");
    }
    catch (IOException ex)
    {
      throw new RemoteDeviceErrorException(MessageFormat.format(Cres.get().getString("devErrConnecting"), getDevice().getDescription() + " (" + getDevice().getInfo() + ")") + ex.getMessage(), ex);
    }
  }
  
  private SSLSocketFactory getTrustedSocketFactory(RemoteServer device)
  {
    return SslHelper.getTrustedSocketFactory(device.isTrustAll(), device.isPreferCrls(), device.isOnlyEndEntity(), device.isNoFallback(), device.isSoftFail());
  }
  
  protected String getConnectionErrorMessage()
  {
    return null;
  }
  
  @Override
  protected boolean loginImpl() throws ContextException
  {
    if (getContextManager() != null)
    {
      getContextManager().restart();
    }
    
    final byte[] token;
    try
    {
      final TokenProvider tokenProvider = getDevice().getAuthTokenProvider();
      token = tokenProvider != null ? tokenProvider.getEncodedToken() : null;
    }
    catch (AggreGateException e)
    {
      throw new ContextException(e);
    }
    
    final DataRecord loginInput = new DataRecord(CommonServerFormats.FIFT_LOGIN);
    loginInput.setValue(RootContextConstants.FIF_LOGIN_USERNAME, getDevice().getUsername());
    if (token == null)
      loginInput.setValue(RootContextConstants.FIF_LOGIN_PASSWORD, getDevice().getPassword());
    else
      loginInput.setValue(RootContextConstants.FIF_LOGIN_TOKEN, new Data(token));
    loginInput.setValue(RootContextConstants.FIF_LOGIN_CODE, getDevice().getCode());
    loginInput.setValue(RootContextConstants.FIF_LOGIN_STATE, null);
    loginInput.setValue(RootContextConstants.FIF_LOGIN_PROVIDER, getDevice().getProvider());
    loginInput.setValue(RootContextConstants.FIF_LOGIN_COUNT_ATTEMPTS, getDevice().isCountAttempts());
    
    DataTable loginResult = callRemoteFunction(Contexts.CTX_ROOT, RootContextConstants.F_LOGIN, null, loginInput.wrap(), null);
    getDevice().setEffectiveUsername(loginResult.rec().getString(RootContextConstants.FOF_LOGIN_USERNAME));
    String login = loginResult.hasField(RootContextConstants.FOF_LOGIN_LOGIN) ? loginResult.rec().getString(RootContextConstants.FOF_LOGIN_LOGIN) : null;
    getDevice().setLogin(login != null ? login : getDevice().getUsername());
    
    if (getContextManager() != null)
    {
      getContextManager().getRoot().reinitialize(); // Resets local cache, because root context was already initialized, but its visible entities changed after login
    }
    
    return true;
  }
  
  @Override
  public void start() throws IOException, InterruptedException, ContextException, RemoteDeviceErrorException
  {
  }
  
  @Override
  protected void disconnectImpl()
  {
    if (dataChannel != null && dataChannel.isOpen())
    {
      try
      {
        dataChannel.close();
      }
      catch (IOException ex)
      {
        Log.PROTOCOL.error("Error closing socket", ex);
      }
    }
    
    dataChannel = null;
    
    super.disconnectImpl();
  }
  
  @Override
  protected void send(OutgoingAggreGateCommand cmd) throws DisconnectionException, IOException
  {
    commandWriter.write(cmd, dataChannel);
  }
  
  protected void setDataChannel(BlockingChannel socketChannel)
  {
    this.dataChannel = socketChannel;
  }
  
  protected BlockingChannel getDataChannel()
  {
    return dataChannel;
  }
  
  @Override
  public boolean isConnected()
  {
    return super.isConnected() && dataChannel != null && dataChannel.isOpen();
  }
  
  public String getAddress()
  {
    return dataChannel != null ? dataChannel.getChannelAddress() : null;
  }
  
  public void startConnectionTimerTask(String name)
  {
    startConnectionTimerTask(name, DefaultClientController.KEEP_ALIVE_PERIOD, DefaultClientController.KEEP_ALIVE_PERIOD);
  }
  
  public void startConnectionTimerTask(String name, long delay, long period)
  {
    connectionTimer = new Timer(CHECK_CONNECTION_TIMER_PREFIX + name);
    TimerTask timerTask = createConnectionTimer();
    connectionTimer.schedule(timerTask, delay, period);
  }
  
  public void removeConnectionTimerTask()
  {
    if (connectionTimer != null)
    {
      connectionTimer.cancel();
      connectionTimer.purge();
    }
  }
  
  public TimerTask createConnectionTimer()
  {
    return new SimpleConnectionTimer(this);
  }
  
  private class SimpleConnectionTimer extends TimerTask
  {
    private final RemoteServerController remoteServerController;
    
    SimpleConnectionTimer(RemoteServerController remoteServerController)
    {
      this.remoteServerController = remoteServerController;
    }
    
    @Override
    public void run()
    {
      try
      {
        OutgoingAggreGateCommand ping = getCommandBuilder().startMessage();
        remoteServerController.sendCommand(ping);
      }
      catch (Exception ex)
      {
        try
        {
          remoteServerController.disconnect();
        }
        catch (IOException | InterruptedException | RemoteDeviceErrorException e)
        {
          throw new RuntimeException(e);
        }
        finally
        {
          this.cancel();
          Log.PROTOCOL.warn("Connection lost", ex);
        }
      }
    }
  }
}
