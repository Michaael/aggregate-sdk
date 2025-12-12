package com.tibbo.aggregate.client.guibuilder;

import java.awt.datatransfer.*;

import com.tibbo.aggregate.common.widget.component.*;

public interface CustomTransferHandler
{
  /**
   * Implements the same functionality as similar method in <code>javax.swing.TransferHandler</code>
   * 
   * @param importedObj
   *          Transferable
   * @return boolean
   * @see javax.swing.TransferHandler#canImport
   */
  public boolean canImport(Transferable importedObj);
  
  /**
   * Implements import operation for transfered object
   * 
   * @param importedObj
   *          Transferable
   */
  public void doImport(Transferable importedObj);
  
  /**
   * Returns string description for import accepted in <code>canImport</code> method. This description can be exposed like tooltip during dnd. Html tags are allowed. For example you can split
   * description into several lines by ' <br>
   * ' tag.
   * 
   * @param importedObj
   *          Transferable
   * @return String
   */
  public String getImportSuggest(Transferable importedObj);
  
  public TemplateResourceNode insertNewComponent(AggreGateIDE guiBuilder, String key, WContainer component);
}
