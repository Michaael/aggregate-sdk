package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.validator.*;

public class DataBean extends AggreGateBean
{
    public static TableFormat FORMAT = new TableFormat(1, 1);
    static  {
        FieldFormat ff = FieldFormat.create("<" + "name" + "><S><F=C><D=" + Cres.get().getString("name") + ">");
        ff.setHelp(Cres.get().getString("conNameChangeWarning"));
        ff.getValidators().add(ValidatorHelper.NAME_LENGTH_VALIDATOR);
        ff.getValidators().add(ValidatorHelper.NAME_SYNTAX_VALIDATOR);
        FORMAT.addField(ff);

        ff = FieldFormat.create("<" + "description" + "><S><F=C><D=" + Cres.get().getString("description") + ">");
        ff.getValidators().add(ValidatorHelper.DESCRIPTION_LENGTH_VALIDATOR);
        ff.getValidators().add(ValidatorHelper.DESCRIPTION_SYNTAX_VALIDATOR);
        FORMAT.addField(ff);
    }

    private String name;
    private String description;

    public DataBean()
    {
        super(FORMAT);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}