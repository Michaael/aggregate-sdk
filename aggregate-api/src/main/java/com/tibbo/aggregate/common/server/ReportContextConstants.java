package com.tibbo.aggregate.common.server;

public interface ReportContextConstants
{
  
  String V_PARAMETERS = "parameters";
  String V_HISTORY = "history";
  String V_RESOURCES = "resources";
  String V_SUBREPORTS = "subreports";
  String V_PDF_FONT_PARAMETERS = "PDFFontParameters";
  
  String F_EXPORT = "export";
  String F_SEND_BY_MAIL = "sendByMail";
  String F_EXPORT_REMOTE = "exportRemote";
  
  String A_SEND_BY_MAIL = "sendByMail";
  String A_VIEW_DATA = "viewData";
  String A_VIEW_HISTORY = "viewHistory";
  String A_EDIT_TEMPLATE = "editTemplate";
  String A_SHOW_REPORT = "show";
  String A_EXPORT = "export";
  String A_EXPORT_REPORT = "rptExportToClientFile";
  
  String VF_PARAMETERS_VALUE = "value";
  String VF_PARAMETERS_PARAMETER = "parameter";
  
  String VF_RESOURCES_TYPE = "type";
  String VF_RESOURCES_RESOURCE = "resource";
  String VF_RESOURCES_NAME = "name";
  String VF_RESOURCES_EXPRESSION = "expression";
  
  String VF_SUBREPORTS_NAME = "name";
  String VF_SUBREPORTS_TEMPLATE_TYPE = "templateType";
  String VF_SUBREPORTS_TEMPLATE = "template";
  String VF_SUBREPORTS_TEMPLATE_EXPRESSION = "templateExpression";
  String VF_SUBREPORTS_DATA_TYPE = "dataType";
  String VF_SUBREPORTS_DATA_EXPRESSION = "dataExpression";
  
  String VF_HISTORY_DATE = "date";
  String VF_HISTORY_USER = "user";
  String VF_HISTORY_REPORT = "report";
  
  String VF_PDF_ENCODING = "PDFEncoding";
  String VF_PDF_FONT = "PDFFont";
  String VF_PDF_EMBED_FONT = "embedFont";
  
  String FIF_EXPORT_FILE = "file";
  String FIF_EXPORT_FORMAT = "format";
  String FIF_EXPORT_CSV_FIELD_DELIMITER = "fieldDelimiter";
  String FIF_EXPORT_TARGET_CONTEXT_MASK = "targetContextMask";
  
  String FIF_SEND_BY_MAIL_RECIPIENTS = "recipients";
  String FIF_SEND_BY_MAIL_FORMAT = "format";
  String FIF_SEND_BY_MAIL_CSV_FIELD_DELIMITER = "fieldDelimiter";
  String FIF_SEND_BY_MAIL_TARGET_CONTEXT_MASK = "targetContextMask";

  String FIF_SEND_BY_MAIL_FILE_NAME = "fileName";
  
  String FOF_EXPORT_REPORT_VALUE = "value";
  
  Integer TYPE_STATIC = 0;
  Integer TYPE_DYNAMIC = 1;
  
  String JASPER_REPORTS_FONTS_PACK_FAMILY = "DejaVu Sans";
  
  String CHARACTER_ENCODING_CP1251 = "Cp1251";
  String CHARACTER_ENCODING_CP1252 = "Cp1252";
  
  String JASPER_REPORTS_PDF_FONT_COURIER = "Courier";
  String JASPER_REPORTS_PDF_FONT_COURIER_BOLD = "Courier-Bold";
  String JASPER_REPORTS_PDF_FONT_COURIER_BOLD_OBLIQUE = "Courier-BoldOblique";
  String JASPER_REPORTS_PDF_FONT_HELVETICA = "Helvetica";
  String JASPER_REPORTS_PDF_FONT_HELVETICA_BOLD = "Helvetica-Bold";
  String JASPER_REPORTS_PDF_FONT_HELVETICA_BOLD_OBLIQUE = "Helvetica-BoldOblique";
  String JASPER_REPORTS_PDF_FONT_HELVETICA_OBLIQUE = "Helvetica-Oblique";
  String JASPER_REPORTS_PDF_FONT_SYMBOL = "Symbol";
  String JASPER_REPORTS_PDF_FONT_TIMES_ROMAN = "Times-Roman";
  String JASPER_REPORTS_PDF_FONT_TIMES_BOLD = "Times-Bold";
  String JASPER_REPORTS_PDF_FONT_TIMES_BOLD_ITALIC = "Times-BoldItalic";
  String JASPER_REPORTS_PDF_FONT_TIMES_ITALIC = "Times-Italic";
  
  String JASPER_REPORTS_PDF_FONTS_PACK_FAMILY = JASPER_REPORTS_PDF_FONT_HELVETICA;
  String JASPER_REPORTS_DEFAULT_PDF_CHARACTER_ENCODING = CHARACTER_ENCODING_CP1252;
  String JASPER_REPORTS_DEFAULT_PDF_DEFAULT_EMBEDDED_FONTS = "true";
  
  String DELIMITER_COMMA = ",";
  String DELIMITER_SEMICOLON = ";";
  String DELIMITER_TAB = "\t";
  String DELIMITER_SPACE = " ";
}