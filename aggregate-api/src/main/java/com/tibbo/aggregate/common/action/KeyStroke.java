package com.tibbo.aggregate.common.action;

public class KeyStroke
{
  private javax.swing.KeyStroke keyStroke;
  
  protected KeyStroke()
  {
  }
  
  public KeyStroke(String keyStroke)
  {
    this.keyStroke = javax.swing.KeyStroke.getKeyStroke(keyStroke);
  }
  
  protected KeyStroke(javax.swing.KeyStroke keyStroke)
  {
    if (keyStroke == null)
    {
      throw new IllegalArgumentException("KeyStroke is null");
    }
    this.keyStroke = keyStroke;
  }
  
  public static KeyStroke getKeyStroke(char keyChar)
  {
    return new KeyStroke(javax.swing.KeyStroke.getKeyStroke(keyChar));
  }
  
  public static KeyStroke getKeyStroke(Character keyChar, int modifiers)
  {
    return new KeyStroke(javax.swing.KeyStroke.getKeyStroke(keyChar, modifiers));
  }
  
  public static KeyStroke getKeyStroke(int keyCode, int modifiers, boolean onKeyRelease)
  {
    return new KeyStroke(javax.swing.KeyStroke.getKeyStroke(keyCode, modifiers, onKeyRelease));
  }
  
  public static KeyStroke getKeyStroke(int keyCode, int modifiers)
  {
    return new KeyStroke(javax.swing.KeyStroke.getKeyStroke(keyCode, modifiers));
  }
  
  public static KeyStroke getKeyStroke(String s)
  {
    javax.swing.KeyStroke k = javax.swing.KeyStroke.getKeyStroke(s);
    if (k == null)
    {
      return null;
    }
    return new KeyStroke(k);
  }
  
  protected javax.swing.KeyStroke getKeyStroke()
  {
    return keyStroke;
  }
  
  @Override
  public String toString()
  {
    return keyStroke.toString();
  }
}
