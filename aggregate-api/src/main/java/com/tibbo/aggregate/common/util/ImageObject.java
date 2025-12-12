package com.tibbo.aggregate.common.util;

import com.google.common.base.CaseFormat;
import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.data.Data;
import com.tibbo.aggregate.common.datatable.AggreGateBean;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableConversion;
import com.tibbo.aggregate.common.datatable.DataTableException;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;

public class ImageObject extends AggreGateBean
{
  public static final String VF_IMAGE_TYPE = "imageType";
  public static final String VF_IMAGE_DATA = "imageData";
  public static final String VF_IMAGE_URL = "imageUrl";
  
  public static final TableFormat VFT_IMAGE = new TableFormat(1, 1);
  
  static
  {
    FieldFormat ff = FieldFormat.create(VF_IMAGE_TYPE, FieldFormat.INTEGER_FIELD, Cres.get().getString("type"));
    for (ImageOptionType imageOptionType : ImageOptionType.values())
    {
      ff.addSelectionValue(imageOptionType.getId(), Cres.get().getString(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, imageOptionType.name())));
    }
    ff.setDefault(ImageOptionType.DATA.getId());
    VFT_IMAGE.addField(ff);
    
    VFT_IMAGE.addField(FieldFormat.create(VF_IMAGE_DATA, FieldFormat.DATA_FIELD, Cres.get().getString("imageData"), new Data()));
    
    VFT_IMAGE.addField(FieldFormat.create(VF_IMAGE_URL, FieldFormat.STRING_FIELD, Cres.get().getString("imageUrl"), ""));
  }
  
  private Integer imageType;
  private Data imageData;
  private String imageUrl;
  
  public ImageObject()
  {
    super(VFT_IMAGE);
  }
  
  public Integer getImageType()
  {
    return imageType;
  }
  
  public void setImageType(Integer imageType)
  {
    this.imageType = imageType;
  }
  
  public Data getImageData()
  {
    return imageData;
  }
  
  public void setImageData(Data imageData)
  {
    this.imageData = imageData;
  }
  
  public String getImageUrl()
  {
    return imageUrl;
  }
  
  public void setImageUrl(String imageUrl)
  {
    this.imageUrl = imageUrl;
  }
  
  public DataTable toDataTable()
  {
    try
    {
      return DataTableConversion.beanToTable(this, VFT_IMAGE);
    }
    catch (DataTableException e)
    {
      throw new RuntimeException(e);
    }
  }
  
  public static ImageObject fromDataTable(DataTable dataTable)
  {
    if (dataTable == null)
    {
      return null;
    }
    
    ImageObject imageObject;
    try
    {
      imageObject = DataTableConversion.beanFromTable(dataTable, ImageObject.class, VFT_IMAGE, false);
    }
    catch (DataTableException e)
    {
      throw new RuntimeException(e);
    }
    return imageObject;
  }
  
  public enum ImageOptionType
  {
    DATA(0),
    URL(1);
    
    private final Integer id;
    
    ImageOptionType(Integer id)
    {
      this.id = id;
    }
    
    public Integer getId()
    {
      return id;
    }
  }
}
