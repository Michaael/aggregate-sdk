package com.tibbo.aggregate.common.server;

public interface AlertContextConstants
{
  public static final int INSTANCE_TYPE_PENDING = 0;
  public static final int INSTANCE_TYPE_ACTIVE = 1;
  
  public static final int STATUS_ENABLED = 0;
  public static final int STATUS_DISABLED = 1;
  public static final int STATUS_ACTIVE = 2;
  public static final int STATUS_ESCALATED = 3;
  
  public static final int CORRECTIVE_ACTION_TYPE_RISE = 0;
  public static final int CORRECTIVE_ACTION_TYPE_ACTIVATION = 1;
  public static final int CORRECTIVE_ACTION_TYPE_DEACTIVATION = 2;
  public static final int CORRECTIVE_ACTION_TYPE_ESCALATION = 3;
  public static final int CORRECTIVE_ACTION_TYPE_DEESCALATION = 4;
  public static final int CORRECTIVE_ACTION_TYPE_ACKNOWLEDGEMENT = 5;
  
  public static final String V_ALERT_ACTIONS = "alertActions";
  public static final String V_ESCALATION = "escalation";
  public static final String V_EVENT_TRIGGERS = "eventTriggers";
  public static final String V_VARIABLE_TRIGGERS = "variableTriggers";
  public static final String V_STATUS = "status";
  public static final String V_EVENT_TRIGGER_STATUS = "eventTriggerStatus";
  public static final String V_VARIABLE_TRIGGER_STATUS = "variableTriggerStatus";
  public static final String V_NOTIFICATIONS = "notifications";
  public static final String V_INTERACTIVE_ACTIONS = "interactiveActions";
  public static final String V_ACTIVE_INSTANCES = "activeInstances";
  public static final String V_PERSISTENT_STATUS = "persistentStatus";

  public static final String E_ALERT = "alert";
  public static final String E_ALERTNOTIFY = "alertnotify";
  public static final String E_DEACTIVATION = "deactivation";

  public static final String A_PENDING_ALERTS = "pendingAlerts";

  public static final String VF_ALERT_ACTIONS_EXECUTION_TYPE = "executionType";
  public static final String VF_ALERT_ACTIONS_MASK = "mask";
  public static final String VF_ALERT_ACTIONS_ACTION = "action";
  public static final String VF_ALERT_ACTIONS_INPUT = "input";
  public static final String VF_ALERT_ACTIONS_CONDITION = "condition";
  public static final String VF_ALERT_ACTIONS_RUN_FROM_SOURCE = "runFromSource";

  public static final String VF_INTERACTIVE_ACTIONS_EXECUTION_TYPE = "executionType";
  public static final String VF_INTERACTIVE_ACTIONS_MASK = "mask";
  public static final String VF_INTERACTIVE_ACTIONS_ACTION = "action";
  public static final String VF_INTERACTIVE_ACTIONS_INPUT = "input";
  public static final String VF_INTERACTIVE_ACTIONS_RUN_FROM_SOURCE = "runFromSource";

  public static final String VF_STATUS_ENABLED = "enabled";
  public static final String VF_STATUS_PENDING_INSTANCE_COUNT = "pendingInstanceCount";
  public static final String VF_STATUS_MAX_PENDING_TIME = "maxPendingTime";
  public static final String VF_STATUS_ESCALATED = "escalated";
  public static final String VF_STATUS_ESCALATION_REASON = "escalationReason";

  public static final String VF_ACTIVE_INSTANCES_EVENT = "event";
  public static final String VF_ACTIVE_INSTANCES_TYPE = "type";
  public static final String VF_ACTIVE_INSTANCES_TIME = "time";
  public static final String VF_ACTIVE_INSTANCES_LEVEL = "level";
  public static final String VF_ACTIVE_INSTANCES_SOURCE = "source";
  public static final String VF_ACTIVE_INSTANCES_KEY = "key";
  public static final String VF_ACTIVE_INSTANCES_CAUSE = "cause";
  public static final String VF_ACTIVE_INSTANCES_MESSAGE = "message";
  public static final String VF_ACTIVE_INSTANCES_TRIGGER = "trigger";
  public static final String VF_ACTIVE_INSTANCES_DATA = "data";

  public static final String VF_EVENT_TRIGGER_STATUS_TRIGGER = "trigger";
  public static final String VF_EVENT_TRIGGER_STATUS_ACTIVE = "active";
  public static final String VF_EVENT_TRIGGER_STATUS_DETAILS = "details";

  public static final String VF_EVENT_TRIGGER_STATUS_DETAILS_CONTEXT = "context";
  public static final String VF_EVENT_TRIGGER_STATUS_DETAILS_KEY = "key";
  public static final String VF_EVENT_TRIGGER_STATUS_DETAILS_ACTIVE = "active";
  public static final String VF_EVENT_TRIGGER_STATUS_DETAILS_EVENTS = "events";

  public static final String VF_VARIABLE_TRIGGER_STATUS_TRIGGER = "trigger";
  public static final String VF_VARIABLE_TRIGGER_STATUS_ACTIVE = "active";
  public static final String VF_VARIABLE_TRIGGER_STATUS_DETAILS = "details";

  public static final String VF_VARIABLE_TRIGGER_STATUS_DETAILS_CONTEXT = "context";
  public static final String VF_VARIABLE_TRIGGER_STATUS_DETAILS_KEY = "key";
  public static final String VF_VARIABLE_TRIGGER_STATUS_DETAILS_ACTIVE = "active";
  public static final String VF_VARIABLE_TRIGGER_STATUS_DETAILS_TIME = "time";
  public static final String VF_VARIABLE_TRIGGER_STATUS_DETAILS_FLAPPING = "flapping";

  public static final String VF_NOTIFICATIONS_NOTIFY_OWNER = "notifyOwner";
  public static final String VF_NOTIFICATIONS_NOTIFICATION_NECESSITY_EXPRESSION = "notificationNecessityExpression";
  public static final String VF_NOTIFICATIONS_ACK_REQUIRED = "ackRequired";
  public static final String VF_NOTIFICATIONS_LIFETIME = "lifetime";
  public static final String VF_NOTIFICATIONS_SOUND = "sound";
  public static final String VF_NOTIFICATIONS_MAIL_TO_OWNER = "mailToOwner";
  public static final String VF_NOTIFICATIONS_MAIL_RECIPIENTS = "mailRecipients";
  public static final String VF_NOTIFICATIONS_ADDITIONAL_MAIL_RECIPIENTS = "additionalMailRecipients";
  public static final String VF_NOTIFICATIONS_SMS_RECIPIENTS = "smsRecipients";
  public static final String VF_NOTIFICATIONS_SMS_RECIPIENTS_PHONE = "phone";
  public static final String VF_NOTIFICATIONS_MAIL_RECIPIENTS_USERNAME = "username";

  public static final String VF_PERSISTENT_STATUS_EVENT_TRIGGERS = "eventTriggers";

  public static final String VF_PERSISTENT_STATUS_EVENT_TRIGGERS_BEAN = "bean";
  public static final String VF_PERSISTENT_STATUS_EVENT_TRIGGERS_CONTEXTS = "contexts";

  public static final String VF_PERSISTENT_STATUS_EVENT_TRIGGERS_CONTEXTS_CONTEXT = "context";
  public static final String VF_PERSISTENT_STATUS_EVENT_TRIGGERS_CONTEXTS_STATUSES = "statuses";

  public static final String VF_PERSISTENT_STATUS_EVENT_TRIGGERS_CONTEXTS_STATUSES_KEY = "key";
  public static final String VF_PERSISTENT_STATUS_EVENT_TRIGGERS_CONTEXTS_STATUSES_ALERT = "alertID";
  public static final String VF_PERSISTENT_STATUS_EVENT_TRIGGERS_CONTEXTS_STATUSES_ACTIVE = "active";
  public static final String VF_PERSISTENT_STATUS_EVENT_TRIGGERS_CONTEXTS_STATUSES_EVENTS = "events";

  public static final String VF_PERSISTENT_STATUS_EVENT_TRIGGERS_CONTEXTS_STATUSES_EVENTS_ID = "eventID";

  public static final String VF_PERSISTENT_STATUS_VARIABLE_TRIGGERS = "variableTriggers";

  public static final String VF_PERSISTENT_STATUS_VARIABLE_TRIGGERS_BEAN = "bean";
  public static final String VF_PERSISTENT_STATUS_VARIABLE_TRIGGERS_CONTEXTS = "contexts";

  public static final String VF_PERSISTENT_STATUS_VARIABLE_TRIGGERS_CONTEXTS_CONTEXT = "context";
  public static final String VF_PERSISTENT_STATUS_VARIABLE_TRIGGERS_CONTEXTS_STATUSES = "statuses";

  public static final String VF_PERSISTENT_STATUS_VARIABLE_TRIGGERS_CONTEXTS_STATUSES_KEY = "key";
  public static final String VF_PERSISTENT_STATUS_VARIABLE_TRIGGERS_CONTEXTS_STATUSES_ALERT = "alertID";
  public static final String VF_PERSISTENT_STATUS_VARIABLE_TRIGGERS_CONTEXTS_STATUSES_ACTIVE = "active";
  public static final String VF_PERSISTENT_STATUS_VARIABLE_TRIGGERS_CONTEXTS_STATUSES_TIME = "time";
  public static final String VF_PERSISTENT_STATUS_VARIABLE_TRIGGERS_CONTEXTS_STATUSES_FLAPPING = "flapping";
  public static final String VF_PERSISTENT_STATUS_VARIABLE_TRIGGERS_CONTEXTS_STATUSES_FLAPPING_ALERT = "flappingAlertID";

  public static final String VF_PERSISTENT_STATUS_ESCALATED = "escalated";

  public static final String EF_ALERT_DESCRIPTION = "description";
  public static final String EF_ALERT_CONTEXT = "context";
  public static final String EF_ALERT_ENTITY = "entity";
  public static final String EF_ALERT_CAUSE = "cause";
  public static final String EF_ALERT_MESSAGE = "message";
  public static final String EF_ALERT_TRIGGER = "trigger";
  public static final String EF_ALERT_DATA = "data";
  public static final String EF_ALERT_DURATION = "duration";
  
  public static final String EF_DEACTIVATION_ID = "id";
  public static final String EF_DEACTIVATION_CONTEXT = "context";
  public static final String EF_DEACTIVATION_DURATION = "duration";
  public static final String EF_DEACTIVATOR_DATA = "deactivatorData";
  
  public static final String EF_ALERTNOTIFY_DESCRIPTION = "description";
  public static final String EF_ALERTNOTIFY_CONTEXT = "context";
  public static final String EF_ALERTNOTIFY_ENTITY = "entity";
  public static final String EF_ALERTNOTIFY_CAUSE = "cause";
  public static final String EF_ALERTNOTIFY_MESSAGE = "message";
  public static final String EF_ALERTNOTIFY_TRIGGER = "trigger";
  public static final String EF_ALERTNOTIFY_DATA = "data";
  public static final String EF_ALERTNOTIFY_ALERT_EVENT_ID = "alertEventId";
  
}
