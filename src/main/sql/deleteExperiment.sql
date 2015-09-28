DELETE
FROM adm_experimentparameters
USING adm_experiment
WHERE adm_experimentparameters.experiment_id='_99fdee10_9e60_4145_a88b_e284e33d7aa5';

DELETE
FROM anl_timeseries
USING anl_analysis,adm_experiment
WHERE anl_timeseries.analysis_id=anl_analysis.id
AND anl_analysis.experiment_id='_99fdee10_9e60_4145_a88b_e284e33d7aa5';

DELETE
FROM anl_analysis
USING adm_experiment
WHERE anl_analysis.experiment_id='_99fdee10_9e60_4145_a88b_e284e33d7aa5';

DELETE
FROM smp_sample
USING adm_experiment
WHERE smp_sample.experiment_id='_99fdee10_9e60_4145_a88b_e284e33d7aa5';

DELETE
FROM adm_experiment
WHERE adm_experiment.id='_99fdee10_9e60_4145_a88b_e284e33d7aa5';

-- DELETE
-- FROM adm_instrumentused
-- WHERE adm_instrumentused.set_id='_99fdee10_9e60_4145_a88b_e284e33d7aa5';

-- DELETE
-- FROM adm_experimentset
-- WHERE adm_experimentset.id='_99fdee10_9e60_4145_a88b_e284e33d7aa5';