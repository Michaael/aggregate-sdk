package com.tibbo.aggregate.common.action;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.action.command.ActivateDashboard;
import com.tibbo.aggregate.common.action.command.Browse;
import com.tibbo.aggregate.common.action.command.Confirm;
import com.tibbo.aggregate.common.action.command.EditCode;
import com.tibbo.aggregate.common.action.command.EditData;
import com.tibbo.aggregate.common.action.command.EditExpression;
import com.tibbo.aggregate.common.action.command.EditGridDashboard;
import com.tibbo.aggregate.common.action.command.EditProperties;
import com.tibbo.aggregate.common.action.command.EditPropertiesResult;
import com.tibbo.aggregate.common.action.command.EditReport;
import com.tibbo.aggregate.common.action.command.EditTemplate;
import com.tibbo.aggregate.common.action.command.EditText;
import com.tibbo.aggregate.common.action.command.LaunchProcessControlProgram;
import com.tibbo.aggregate.common.action.command.LaunchWidget;
import com.tibbo.aggregate.common.action.command.OpenGridDashboard;
import com.tibbo.aggregate.common.action.command.SelectEntities;
import com.tibbo.aggregate.common.action.command.ShowDiff;
import com.tibbo.aggregate.common.action.command.ShowError;
import com.tibbo.aggregate.common.action.command.ShowEventLog;
import com.tibbo.aggregate.common.action.command.ShowGuide;
import com.tibbo.aggregate.common.action.command.ShowMessage;
import com.tibbo.aggregate.common.action.command.ShowReport;
import com.tibbo.aggregate.common.action.command.ShowSystemTree;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextRuntimeException;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.context.EntityList;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.device.DisconnectionException;
import com.tibbo.aggregate.common.event.EventLevel;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.server.ServerContext;
import com.tibbo.aggregate.common.util.ComponentLocation;
import com.tibbo.aggregate.common.util.DashboardProperties;
import com.tibbo.aggregate.common.util.Log4jLevelHelper;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.WebWindowLocation;
import com.tibbo.aggregate.common.util.WindowLocation;

public class ServerActionCommandProcessor {
    private final ServerAction action;

    public ServerActionCommandProcessor(ServerAction action) {
        if (action == null) {
            throw new NullPointerException();
        }

        this.action = action;
    }

    static DataTable getExecutionParameter(DataTable input, String name) {
        if (input != null && input.getRecordCount() >= 1) {
            if (input.rec().hasField(ActionUtils.FIELD_ACTION_EXECUTION_PARAMETERS)) {
                DataTable executionParameters = input.rec().getDataTable(ActionUtils.FIELD_ACTION_EXECUTION_PARAMETERS);
                if (executionParameters != null && executionParameters.getRecordCount() > 0 && executionParameters.getFormat().hasField(name)) {
                    return executionParameters.rec().getDataTable(name);
                }
            }

            if (input.rec().hasField(name)) {
                if (input.getFormat(name).getType() == FieldFormat.DATATABLE_FIELD)
                    return input.rec().getDataTable(name);
                return null;
            }
        }

        return null;
    }

    public ServerContext fetchDnDSourceContext(String title, ActionDefinition actionDefinition, ServerActionInput actionParams, CallerController callerController) throws ContextException {
        List<ServerContext> cons = fetchDnDSourceContexts(title, actionDefinition, actionParams, callerController);
        return cons.size() == 0 ? null : cons.get(0);
    }

    public ServerContext fetchDnDSourceContext(String title, ActionDefinition actionDefinition, ServerActionInput actionParams, CallerController callerController, String expandedContext)
            throws ContextException {
        List<ServerContext> cons = fetchDnDSourceContexts(title, actionDefinition, actionParams, callerController, expandedContext);
        return cons.size() == 0 ? null : cons.get(0);
    }

    public List<ServerContext> fetchDnDSourceContexts(String title, ActionDefinition actionDefinition, ServerActionInput actionParams, CallerController callerController) throws ContextException {
        return fetchDnDSourceContexts(title, actionDefinition, actionParams, callerController, null);
    }

    public List<ServerContext> fetchDnDSourceContexts(String title, ActionDefinition actionDefinition, ServerActionInput actionParams, CallerController callerController, String expandedContext)
            throws ContextException {
        ServerContext result = getDnDSourceContext(actionParams, callerController);

        if (result != null) {
            return Collections.singletonList(result);
        }

        RequestIdentifier id = new RequestIdentifier(ActionUtils.FIELD_ACTION_FROM_CONTEXT);

        List<ResourceMask> dropSources = actionDefinition.getDropSources();

        List<String> contextTypes = null;
        if (dropSources != null) {
            contextTypes = new LinkedList();
            for (ResourceMask dropSource : dropSources) {
                contextTypes.add(dropSource.toString());
            }
        }

        List<Reference> refs = selectEntities(id, title, contextTypes, Contexts.CTX_ROOT, null, expandedContext, true, false, false, false, false, false, false, true, null);

        if (refs.size() > 0) {
            List<ServerContext> res = new LinkedList();
            List<String> paths = new LinkedList();
            for (Reference ref : refs) {
                paths.add(ref.getContext());
                ServerContext<ServerContext> definingContext = action.getDefiningContext();
                ServerContext cur = definingContext.get(ref.getContext(), callerController);
                if (cur != null) {
                    res.add(cur);
                }
            }

            if (res.size() == 0) {
                throw new ContextException(Cres.get().getString("conNotAvail") + StringUtils.print(paths));
            }

            return res;
        } else {
            return Collections.emptyList();
        }
    }

    public ServerContext getDnDSourceContext(ServerActionInput actionParams, CallerController callerController) {
        if (actionParams.getData().getRecordCount() == 1) {
            String path = null;

            if (actionParams.getData().getFormat().hasField(ActionUtils.FIELD_ACTION_FROM_CONTEXT)) {
                path = actionParams.getData().rec().getString(ActionUtils.FIELD_ACTION_FROM_CONTEXT);
            }

            if (path != null) {
                ServerContext<ServerContext> definingContext = action.getDefiningContext();
                return definingContext.get(path, callerController);
            }
        }

        return null;
    }

    public GenericActionResponse send(GenericActionCommand cmd) {
        try {
            return action.send(cmd);
        } catch (DisconnectionException ex) {
            throw new ContextRuntimeException(ex);
        }
    }

    public boolean confirm(String message) {
        GenericActionCommand cmd = new Confirm(Cres.get().getString("confirmation"), message, ActionUtils.YES_NO_OPTION, ActionUtils.QUESTION_MESSAGE);
        GenericActionResponse resp = send(cmd);
        return Confirm.parseConfirm(resp) == ActionUtils.YES_OPTION;
    }

    public int confirm(RequestIdentifier id, boolean group, String title, String message, int optionType, int messageType) {
        GenericActionCommand cmd = new Confirm(title, message, optionType, messageType);
        cmd.setRequestId(id);
        cmd.setBatchEntry(group);
        GenericActionResponse resp = send(cmd);
        return Confirm.parseConfirm(resp);
    }

    public DataTable editData(String title, DataTable data, boolean showResultInDockableFrame, boolean readonly) {
        return editData(null, null, false, title, data, showResultInDockableFrame, readonly, null, null, null, null, null, null, null, null, null);
    }

    public DataTable editData(String title, DataTable data, boolean showResultInDockableFrame, boolean readonly, String iconId, String helpId, String help, WindowLocation location,
                              DashboardProperties dashboard, ComponentLocation componentLocation) {
        return editData(null, null, false, title, data, showResultInDockableFrame, readonly, iconId, helpId, help, null, location, dashboard, null, null, componentLocation);
    }

    public DataTable editData(String title, DataTable data, boolean showResultInDockableFrame, boolean readonly, String iconId, String helpId, String help, Context defaultContext,
                              WindowLocation location, DashboardProperties dashboard, String expression, Long period, ComponentLocation componentLocation) {
        return editData(null, null, false, title, data, showResultInDockableFrame, readonly, iconId, helpId, help, defaultContext, location, dashboard, expression, period, componentLocation);
    }

    public DataTable editData(String title, DataTable data, String iconId, String helpId, String help, ComponentLocation componentLocation) {
        return editData(null, null, false, title, data, false, false, iconId, helpId, help, null, null, null, null, null, componentLocation);
    }

    public DataTable editData(String title, DataTable data, boolean readonly, String helpId, String help, ComponentLocation componentLocation) {
        return editData(null, null, false, title, data, false, readonly, null, helpId, help, null, null, null, null, null, componentLocation);
    }

    public DataTable editData(String title, DataTable data, ComponentLocation componentLocation) {
        return editData(null, null, false, title, data, componentLocation);
    }

    public DataTable editData(RequestIdentifier id, String title, DataTable data) {
        return editData(id, null, false, title, data, false, false, null, null, null, null, null, null, null, null, null);
    }

    public DataTable editData(RequestIdentifier id, EditDataMerger merger, boolean group, String title, DataTable data, ComponentLocation componentLocation) {
        return editData(id, merger, group, title, data, false, false, null, null, null, null, null, null, null, null, componentLocation);
    }

    public DataTable editData(RequestIdentifier id, EditDataMerger merger, boolean group, String title, DataTable data, boolean useDockableFrame, boolean readonly, String iconId, String helpId,
                              String help, Context defaultContext, WindowLocation location, DashboardProperties dashboard, String expression, Long period, ComponentLocation componentLocation) {
        final boolean showToolbar = true;
        final Boolean showHeader = null;

        return editData(id, merger, group, title, data, useDockableFrame, readonly, iconId, helpId, help, defaultContext, location, dashboard, expression, period, showToolbar, showHeader, null, null, componentLocation);
    }

    public DataTable editData(RequestIdentifier id, EditDataMerger merger, boolean group, String title, DataTable data, boolean useDockableFrame, boolean readonly, String iconId, String helpId,
                              String help, Context defaultContext, WindowLocation location, DashboardProperties dashboard, String expression, Long period, boolean showToolbar, Boolean showHeader, Boolean showLineNumbers,
                              Boolean horizontalScrolling, ComponentLocation componentLocation) {
        String defaultContextPath = defaultContext != null ? defaultContext.getPath() : null;

        EditData cmd = new EditData(title, data, readonly);
        cmd.setUseDockableFrame(useDockableFrame);
        cmd.setIconId(iconId);
        cmd.setHelpId(helpId);
        cmd.setHelp(help);
        cmd.setDefaultContext(defaultContextPath);
        cmd.setLocation(location);
        cmd.setDashboard(dashboard);
        cmd.setExpression(expression);
        cmd.setPeriod(period);

        cmd.setRequestId(id);
        cmd.setBatchEntry(group);
        cmd.setShowToolbar(showToolbar);
        cmd.setShowHeader(showHeader);
        cmd.setShowLineNumbers(showLineNumbers);
        cmd.setHorizontalScrolling(horizontalScrolling);
        cmd.setComponentLocation(componentLocation);

        return editData(cmd, merger);
    }

    public DataTable editData(EditData cmd, EditDataMerger merger) {
        GenericActionResponse resp = send(cmd);

        if (resp == null)
            return cmd.getData();

        DataTable edited = resp.getParameters();

        if (action.getCallerController().isHeadless()) {
            if (edited == null) {
                return cmd.getData();
            }
            if (merger != null) {
                return merger.merge(edited, cmd.getData());
            }
        }

        return edited;
    }

    public EditPropertiesResult editProperties(String title, boolean readOnly, boolean dynamic, boolean useDockableFrame, boolean singleWindowMode, String contextName, List<String> slaves,
                                               WindowLocation location, DashboardProperties dashboard, String propertiesGroup, ComponentLocation componentLocation, String defaultGroup) {
        EditProperties cmd = new EditProperties(title, contextName, propertiesGroup);
        cmd.setReadOnly(readOnly);
        cmd.setDynamic(dynamic);
        cmd.setUseDockableFrame(useDockableFrame);
        cmd.setSingleWindowMode(singleWindowMode);
        cmd.setContext(contextName);
        cmd.setSlaves(slaves);
        cmd.setLocation(location);
        cmd.setComponentLocation(componentLocation);
        cmd.setDefaultGroup(defaultGroup);
        GenericActionResponse resp = send(cmd);
        return EditPropertiesResult.parse(resp);
    }

    public EditPropertiesResult editProperties(String title, boolean readOnly, boolean dynamic, boolean useDockableFrame, boolean singleWindowMode, String contextName, List<String> slaves,
                                               WindowLocation location, DashboardProperties dashboard, ComponentLocation componentLocation, String defaultGroup, String... properties) {
        EditProperties cmd = new EditProperties(title, contextName, Arrays.asList(properties));
        cmd.setReadOnly(readOnly);
        cmd.setDynamic(dynamic);
        cmd.setUseDockableFrame(useDockableFrame);
        cmd.setSingleWindowMode(singleWindowMode);
        cmd.setContext(contextName);
        cmd.setSlaves(slaves);
        cmd.setLocation(location);
        cmd.setComponentLocation(componentLocation);
        cmd.setDefaultGroup(defaultGroup);
        GenericActionResponse resp = send(cmd);
        return EditPropertiesResult.parse(resp);
    }

    protected String editTemplate(String type, String title, String defaultContext, String widgetContext, String widget, int editMode) {
        GenericActionCommand cmd = new EditTemplate(type, title, defaultContext, widgetContext, widget, editMode);
        GenericActionResponse resp = send(cmd);
        DataTable response = resp.getParameters();

        if (response == null || response.getRecordCount() == 0) {
            return null;
        }

        String result = response.rec().getString(EditTemplate.RF_RESULT);

        if (!(result != null && result.equals(ActionUtils.RESPONSE_SAVED))) {
            return null;
        }

        return response.rec().getString(EditTemplate.RF_WIDGET);
    }

    public String editWidget(String title, String defaultContext, String widgetContext, String widget) {
        return editTemplate(ActionUtils.CMD_EDIT_WIDGET, title, defaultContext, widgetContext, widget, EditTemplate.EDIT_WIDGET);
    }

    public String editProcessControlProgram(String title, String defaultContext, String widgetContext, String widget, int editMode) {
        return editTemplate(ActionUtils.CMD_EDIT_PROCESS_CONTROL_PROGRAM, title, defaultContext, widgetContext, widget, editMode);
    }

    public String editWorkflow(String title, String defaultContext, String widgetContext, String widget) {
        return editTemplate(ActionUtils.CMD_EDIT_WORKFLOW, title, defaultContext, widgetContext, widget, EditTemplate.EDIT_WORKFLOW);
    }

    public void browse(URI url) {
        send(new Browse(url));
    }

    public GenericActionResponse launchWidget(String title, String widgetContext, String defaultContext, String encodedWidgetTemplate, WindowLocation location, DashboardProperties dp, DataTable input,
                                              DataTable widgetSettings, ComponentLocation componentLocation) {
        LaunchWidget cmd = new LaunchWidget(title, widgetContext, defaultContext, encodedWidgetTemplate);
        cmd.setLocation(location);
        cmd.setDashboard(dp);
        cmd.setInput(input);
        cmd.setWidgetSettings(widgetSettings);
        cmd.setComponentLocation(componentLocation);
        return send(cmd);
    }

    public GenericActionResponse launchProcessControlProgram(String title, String widgetContext, String defaultContext, String encodedWidgetTemplate, WindowLocation location, DashboardProperties dp,
                                                             DataTable input) {
        LaunchProcessControlProgram cmd = new LaunchProcessControlProgram(title, widgetContext, defaultContext, encodedWidgetTemplate);
        cmd.setLocation(location);
        cmd.setDashboard(dp);
        cmd.setInput(input);
        return send(cmd);
    }

    public Integer showEventLog(String title, EntityList eventList, boolean showRealtime, boolean showHistory, boolean preloadHistory, boolean showContexts, boolean showNames, boolean showLevels,
                                boolean showAcknowledgements, boolean showEnrichments, Integer customListenerCode, WindowLocation location, DashboardProperties dashboard, ComponentLocation componentLocation) {
        GenericActionResponse resp = send(new ShowEventLog(title, eventList, showRealtime, showHistory, preloadHistory, showContexts, showNames, showLevels, showAcknowledgements, showEnrichments,
                customListenerCode, location, dashboard, componentLocation));

        if (resp == null || resp.getParameters() == null) {
            return -1;
        }

        return resp.getParameters().rec().getInt(ShowEventLog.RF_LISTENER_CODE);
    }

    public DataTable showGuide(String title, String invokerContext, String macroName) {
        GenericActionResponse resp = send(new ShowGuide(title, invokerContext, macroName));
        return resp.getParameters();
    }

    public void showError(String title, int level, String message, Throwable ex) {
        ShowError cmd = new ShowError(title, message, level, ex);
        Log.CONTEXT_ACTIONS.log(Level.toLevel(Log4jLevelHelper.getLog4jLevelByAggreGateLevel(level)), message, ex);
        send(cmd);
    }

    public void showError(Throwable ex) {
        showError(Cres.get().getString("error"), EventLevel.INFO, ex.getMessage(), ex);
    }

    public void showMessage(String message) {
        showMessage(Cres.get().getString("info"), message, EventLevel.INFO);
    }

    public void showMessage(String title, String message) {
        showMessage(title, message, EventLevel.INFO);
    }

    public void showMessage(String title, String message, int level) {
        send(new ShowMessage(title, message, level));
    }

    public void showDiff(String title, String firstFileTitle, String firstFile, String secondFileTitle, String secondFile) {
        send(new ShowDiff(title, firstFileTitle, firstFile, secondFileTitle, secondFile));
    }

    public DataTable editReport(String title, String template, DataTable data, DataTable subreports, DataTable resources) {
        GenericActionCommand cmd = new EditReport(title, template, data, subreports, resources);
        GenericActionResponse resp = send(cmd);
        return resp.getParameters();
    }

    public void showReport(String title, byte[] reportData, WindowLocation location, DashboardProperties dashboard) {
        showReport(title, reportData, location, dashboard, null, null);
    }

    public void showReport(String title, byte[] reportData, WindowLocation location, DashboardProperties dashboard,
                           String reportFormat, String fileName) {
        GenericActionCommand cmd = new ShowReport(title, reportData, location, dashboard, reportFormat, fileName);
        send(cmd);
    }

    public String selectContext(String title, Class contextClass, String defaultContext, String expandedContext, ComponentLocation componentLocation) {
        List<Reference> list = selectEntities(null, title, Collections.singletonList(ContextUtils.getTypeForClass(contextClass)), Contexts.CTX_ROOT, defaultContext, expandedContext, true, false, false,
                false, false, false, true, false, componentLocation);

        return (list != null && list.size() > 0) ? list.get(0).getContext() : null;
    }

    public List<Reference> selectEntities(RequestIdentifier id, String title, Collection<String> contextTypes, String rootContext, String defaultContext, String expandedContext, boolean showChildren,
                                          boolean allowMasks, boolean showVars, boolean showFuncs, boolean showEvents, boolean showFields, boolean singleSelection, boolean useCheckboxes, ComponentLocation componentLocation) {
        GenericActionCommand ac = new SelectEntities(title, contextTypes, rootContext, defaultContext, expandedContext, showChildren, allowMasks, showVars, showFuncs, showEvents, showFields,
                singleSelection, useCheckboxes);
        ac.setRequestId(id);
        ac.setComponentLocation(componentLocation);
        GenericActionResponse resp = send(ac);

        List<Reference> res = new LinkedList();

        DataTable parameters = resp.getParameters();
        if (parameters != null)
        {
            for (DataRecord rec : parameters)
            {
                res.add(new Reference(rec.getString(SelectEntities.RF_REFERENCE)));
            }
        }

        return res;
    }

    public String editText(String title, String text, String mode) {
        GenericActionResponse resp = send(new EditText(title, text, mode));

        DataTable response = resp.getParameters();

        if (response == null) {
            return null;
        }

        String result = response.rec().getString(EditText.RF_RESULT);

        if (!result.equals(ActionUtils.RESPONSE_SAVED)) {
            return null;
        }

        return response.rec().getString(EditText.RF_TEXT);
    }

    public String editCode(String title, String code, String mode) {
        return editCode(title, code, mode, false).rec().getString(EditCode.RF_CODE);
    }

    public DataTable editCode(String title, String code, String mode, boolean compile) {
        GenericActionResponse resp = send(new EditCode(title, code, mode, compile));

        DataTable response = resp.getParameters();

        if (response == null) {
            return null;
        }

        String result = response.rec().getString(EditCode.RF_RESULT);

        if (!result.equals(ActionUtils.RESPONSE_SAVED)) {
            return null;
        }

        return response;
    }

    public void showSystemTree(String title, Context root, ComponentLocation componentLocation) {
        ShowSystemTree sst = new ShowSystemTree(title, root);
        sst.setLocation(new WindowLocation(WindowLocation.STATE_FLOATING));
        sst.setComponentLocation(componentLocation);
        send(sst);
    }

    public void showSystemTree(String title, ComponentLocation componentLocation, String... roots) {
        ShowSystemTree sst = new ShowSystemTree(title, roots);
        sst.setLocation(new WindowLocation(WindowLocation.STATE_FLOATING));
        sst.setComponentLocation(componentLocation);
        send(sst);
    }

    public void showSystemTree(String title, Collection<String> roots, ComponentLocation componentLocation) {
        ShowSystemTree sst = new ShowSystemTree(title, roots.toArray(new String[roots.size()]));
        sst.setLocation(new WindowLocation(WindowLocation.STATE_FLOATING));
        sst.setComponentLocation(componentLocation);
        send(sst);
    }

    public void activateDashboard(String name) {
        send(new ActivateDashboard(name));
    }

    public void activateDashboard(String nameString, String windowPathString) {
        send(new ActivateDashboard(nameString, windowPathString));
    }

    public void activateDashboard(String nameString, DataTable parameters) {
        send(new ActivateDashboard(nameString, parameters));
    }

    public void editDashboard(String contextPath, String defaultContext) {
        send(new EditGridDashboard(contextPath, defaultContext));
    }

    public void openDashboard(String contextPath, String defaultContext, WebWindowLocation location, ComponentLocation componentLocation) {
        OpenGridDashboard o = new OpenGridDashboard(contextPath, defaultContext, location);
        o.setComponentLocation(componentLocation);
        send(o);
    }

    public void openEvaluateExpressionEditorDialog() {
        send(new EditExpression());
    }

}
