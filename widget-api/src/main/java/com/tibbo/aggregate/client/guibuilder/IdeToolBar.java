package com.tibbo.aggregate.client.guibuilder;

import java.util.*;

public interface IdeToolBar
{
  void buttonsEnabledForElements(Set<TemplateResourceNode> selectedNodes);
  
  void syncUndoRedoButtons();
  
  void runPreview();
  
  void setBlocked(boolean b);
}
