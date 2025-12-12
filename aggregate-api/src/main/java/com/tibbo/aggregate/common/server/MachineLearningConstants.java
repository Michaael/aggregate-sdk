package com.tibbo.aggregate.common.server;

public interface MachineLearningConstants
{
  String V_CURRENT_STATE = "currentState";
  String V_INITIAL_TRAINED_STATE = "initialTrainedState";
  
  String F_TRAIN = "train";
  String F_OPERATE = "operate";
  String F_EVALUATE = "evaluate";
  String F_CROSS_VALIDATE = "crossValidate";
  String F_RESET = "reset";
  String F_RESET_EVALUATION = "resetEvaluation";
  
  String VF_STATE_STATE = "state";
  String VF_STATE_DATASET_TABLE_FORMAT = "datasetTableFormat";
  
  String FOF_TRAIN_TRAINED_UNIT_INFO = "trainedUnitInfo";
  String FOF_TRAIN_PARAMETERS = "parameters";
  
  String FOF_EVALUATE_CORRECT = "correct";
  String FOF_EVALUATE_PCT_CORRECT = "pctCorrect";
  String FOF_EVALUATE_INCORRECT = "incorrect";
  String FOF_EVALUATE_PCT_INCORRECT = "pctIncorrect";
  String FOF_EVALUATE_KAPPA = "kappa";
  String FOF_EVALUATE_CORRELATION = "correlation";
  String FOF_EVALUATE_MAE = "meanAbsoluteError";
  String FOF_EVALUATE_RMSE = "rootMeanSquaredError";
  String FOF_EVALUATE_RAE = "relativeAbsoluteError";
  String FOF_EVALUATE_RRSE = "rootRelativeSquaredError";
  
  String FOF_EVALUATE_DETAILED_ACCURACY_TP_RATE = "truePositiveRate";
  String FOF_EVALUATE_DETAILED_ACCURACY_FP_RATE = "falsePositiveRate";
  String FOF_EVALUATE_DETAILED_ACCURACY_PRECISION = "precision";
  String FOF_EVALUATE_DETAILED_ACCURACY_RECALL = "recall";
  String FOF_EVALUATE_DETAILED_ACCURACY_F_MEASURE = "fMeasure";
  String FOF_EVALUATE_DETAILED_ACCURACY_MCC = "matthewsCorrelationCoefficient";
  String FOF_EVALUATE_DETAILED_ACCURACY_ROC_AREA = "rocArea";
  String FOF_EVALUATE_DETAILED_ACCURACY_PRC_AREA = "prcArea";
  String FOF_EVALUATION_INFORMATION = "evaluationInformation";
  
  String FOF_EVALUATE_DETAILED_ACCURACY_CLASS = "class";
  String FOF_EVALUATE_CONFUSION_MATRIX_CLASS = "class";
  
  String FOF_EVALUATE_UNCLASSIFIED = "unclassified";
  String FOF_EVALUATE_PCT_UNCLASSIFIED = "pctUnclassified";
  String FOF_EVALUATE_TOTAL_NUM_INSTANCES = "totalNumInstances";
  
  String FOF_EVALUATE_DETAILED_ACCURACY = "detailedAccuracy";
  String FOF_EVALUATE_CONFUSION_MATRIX = "confusionMatrix";
  
  String FOF_OPERATE_PREDICTED = "predicted";
  String FOF_OPERATE_ERROR = "error";
  String FOF_OPERATE_PROBABILITY = "probability";
  
  String WEIGHTED_AVG = "Weighted Avg.";
  
  String DATASET = "Dataset";
  String CV_SET = "CV Set";
  
  String NORMAL = "normal";
  String ABNORMAL = "abnormal";
  
  int STATUS_UNTRAINED = 0;
  int STATUS_TRAINED = 1;
}
