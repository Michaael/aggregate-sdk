package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.action.ActionUtils;
import com.tibbo.aggregate.common.action.GenericActionCommand;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;

public class EditCode extends GenericActionCommand {
    public static final String CF_CODE = "code";
    public static final String CF_MODE = "mode";

    public static final String CF_COMPILE = "compile";

    public static final String RF_RESULT = "result";
    public static final String RF_CODE = "code";
    public static final String RF_COMPILED_CODE = "compiledCode";

    public static final TableFormat CFT_EDIT_CODE = new TableFormat(1, 1);
    public static final TableFormat RFT_EDIT_CODE = new TableFormat(1, 1);

    static {
        CFT_EDIT_CODE.addField(FieldFormat.create("<" + CF_CODE + "><S>").setEditor(StringFieldFormat.EDITOR_TEXT).setDescription(Cres.get().getString("wCode")));

        FieldFormat<Object> ff = FieldFormat.create("<" + CF_MODE + "><S><F=N>")
                .setSelectionValues(EditText.modes())
                .setExtendableSelectionValues(true)
                .setDefaultOverride(true)
                .setDescription(Cres.get().getString("mode"));
        CFT_EDIT_CODE.addField(ff);

        ff = FieldFormat.create("<" + CF_COMPILE + "><B><F=0>");
        CFT_EDIT_CODE.addField(ff);
    }

    static {
        RFT_EDIT_CODE.addField("<" + RF_RESULT + "><S>");
        RFT_EDIT_CODE.addField("<" + RF_CODE + "><S><F=N>");
        RFT_EDIT_CODE.addField("<" + RF_COMPILED_CODE + "><S><F=N>");
    }

    private String code;
    private String mode;

    private boolean compile;

    public EditCode() {
        super(ActionUtils.CMD_EDIT_CODE, CFT_EDIT_CODE, RFT_EDIT_CODE);
    }

    public EditCode(String title, String code, String mode, boolean compile) {
        super(ActionUtils.CMD_EDIT_CODE, title);
        this.code = code;
        this.mode = mode;
        this.compile = compile;
    }

    public EditCode(String title, DataTable parameters) {
        super(ActionUtils.CMD_EDIT_CODE, title, parameters, CFT_EDIT_CODE);
    }

    @Override
    protected DataTable constructParameters() {
        return new SimpleDataTable(CFT_EDIT_CODE, code, mode, compile);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean getCompile() {
        return this.compile;
    }

    public void setCompile(boolean compile) {
        this.compile = compile;
    }
}
