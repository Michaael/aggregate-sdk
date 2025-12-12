package examples.component;

import com.tibbo.aggregate.common.plugin.*;

public class CustomProgressBar extends ComponentPlugin
{
  
  @Override
  public Class<CustomProgressBarSwingRenderer> getSwingRenderer()
  {
    return CustomProgressBarSwingRenderer.class;
  }
  
  @Override
  public Class<WCustomProgressBar> getWComponent()
  {
    return WCustomProgressBar.class;
  }
  
  @Override
  protected void doStart() throws Exception
  {
    // TODO Auto-generated method stub
  }
  
  @Override
  protected void doStop() throws Exception
  {
    // TODO Auto-generated method stub
  }
  
}
