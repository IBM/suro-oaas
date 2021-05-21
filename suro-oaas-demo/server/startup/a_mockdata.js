templatesMock = [{

    id: '4cb50f88d24aff6d238d401e29009d5e',
    modelId: '4cb50f88d24aff6d238d401e29008aaf',
    revision: '4-74c2217f8e2a4d12b3c478fe7d5f79db',

    label: 'Maximize the performance of the hospital',
    description: 'This template can be used to set the relative weights of different goals and also to change the available resources within the hospital',

    parameters: [{

        name: 'inputWeightOverdue',
        value: 9,
        fixed: false,

    }, {

        name: 'inputWeightOntime',
        value: 0,
        fixed: false,

    }, {

        name: 'inputPerPeriodWaitlistPatientPenalty',
        value: 1,
        fixed: true,

    }, {

        name: 'inputExtraNormalBeds',
        value: 0,
        fixed: false,

    }, {

        name: 'inputExtraIcuBeds',
        value: 0,
        fixed: false,

    }],

}, {

    id: '4cb50f88d24aff6d238d401e2900a5de',
    modelId: '4cb50f88d24aff6d238d401e29008aaf',
    revision: '4-60824f3fd88daf6735fa55e7c9c3cdec',

    label: 'Optimize hospital performance, overdue patients and waiting list',
    description: 'This template maximizes the number of performance points achieved, and minimizes the total number of ' +
            'overdue patients at the end of last period and patients left on the waiting list. The order of KPIs and ' +
            'their bonus and penalty are very critical for the optimization outcome.',

    parameters: [{

        name: 'inputPerformancePointBonus',
        value: 100000,
        fixed: false,

    }, {

        name: 'inputWeightOverdue',
        value: 9,
        fixed: true,

    }, {

        name: 'inputLastPeriodOverduePatientPenalty',
        value: 1,
        fixed: false,

    }, {

        name: 'inputPerPeriodWaitlistPatientPenalty',
        value: 1,
        fixed: true,

    }, {

        name: 'inputExtraNormalBeds',
        value: 0,
        fixed: false,

    }, {

        name: 'inputExtraIcuBeds',
        value: 0,
        fixed: false,

    }],

}, {

    id: '4cb50f88d24aff6d238d401e2900aa3d',
    modelId: '4cb50f88d24aff6d238d401e29008aaf',
    revision: '4-bb761da081526a00572857991a756a8d',

    label: 'Optimize overdue patients, waiting list and the hospital performance',
    description: 'This templates minimizes the total number of overdue patients at the end of last period and patients ' +
            'left on the waiting list and maximizes the number of performance points achieved. The order of KPIs and ' +
            'their bonus and penalty are very critical for the optimization outcome.',

    parameters: [{

        name: 'inputPerformancePointBonus',
        value: 1,
        fixed: true,

    }, {

        name: 'inputWeightOverdue',
        value: 9,
        fixed: true,

    }, {

        name: 'inputLastPeriodOverduePatientPenalty',
        value: 1,
        fixed: false,

    }, {

        name: 'inputPerPeriodWaitlistPatientPenalty',
        value: 10,
        fixed: true,

    }, {

        name: 'inputExtraNormalBeds',
        value: 0,
        fixed: false,

    }, {

        name: 'inputExtraIcuBeds',
        value: 0,
        fixed: false,

    }],

}, {

    id: '4cb50f88d24aff6d238d401e2900b310',
    modelId: '4cb50f88d24aff6d238d401e29008aaf',
    revision: '4-22bf57fa719cd99bf7a3b056df29c315',

    label: 'Optimize the hospital performance, overdue patients, waiting list and initial overdue patients in urgency category 1',
    description: 'This template maximizes the number of performance points achieved, and minimizes the initial overdue ' +
            'patients in urgency category 1, the total number of overdue patients at the end of last period and patients ' +
            'left on the waiting list. The order of KPIs and their bonus and penalty are very critical for the optimization ' +
            'outcome.',

    parameters: [{

        name: 'inputPerformancePointBonus',
        value: 100000,
        fixed: true,

    }, {

        name: 'inputWeightOverdue',
        value: 9,
        fixed: true,

    }, {

        name: 'inputLastPeriodOverduePatientPenalty',
        value: 0,
        fixed: false,

    }, {

        name: 'inputPerPeriodWaitlistPatientPenalty',
        value: 1,
        fixed: true,

    }, {

        name: 'inputUrgencyCategory1PatientPenalty',
        value: 100000,
        fixed: true,

    }, {

        name: 'inputExtraNormalBeds',
        value: 0,
        fixed: false,

    }, {

        name: 'inputExtraIcuBeds',
        value: 0,
        fixed: false,

    }],

}, {

    id: '4cb50f88d24aff6ea38d401e2900b310',
    modelId: '4cb50f88d24aff6d238d401e29008aaf',
    revision: '4-22bf57fa719cd99bf7a3b056df29c315',

    label: 'Flexible (free form)',
    description: 'This template enables you to play with all the available parameters of the model so that you can create runs by having full controls on all the aspects of the model.',

    parameters: [{

        name: 'inputWeightOverdue',
        value: 1,
        fixed: false,

    }, {

        name: 'inputWeightOntime',
        value: 1,
        fixed: false,

    }, {

        name: 'inputExtraNormalBeds',
        value: 1,
        fixed: false,

    }, {

        name: 'inputExtraIcuBeds',
        value: 1,
        fixed: false,

    }, {

        name: 'inputPerformancePointBonus',
        value: 1,
        fixed: false,

    }, {

        name: 'inputLastPeriodOverduePatientPenalty',
        value: 1,
        fixed: false,

    }, {

        name: 'inputPerPeriodWaitlistPatientPenalty',
        value: 1,
        fixed: false,

    }, {

        name: 'inputUrgencyCategory1PatientPenalty',
        value: 1,
        fixed: false,
    }],

}];


/**
 * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * ********************
 */


modelsMock = [{
    id: '4cb50f88d24aff6d238d401e29008aaf',
    revision: '11-461624796410d6f106f14e3dcc54e2ea',
    labels: 'Default',
    modelVersion: 4,
    default: true,
    description: 'Default model for the optimisation.',

    objectives: [{

        name: 'Profit',
        label: 'Maximise Profit',
        description: 'Maximise the expected WIES income from the patients treated',

    }, {

        name: 'points',
        label: 'Maximise Points',
        description: 'Maximise the number of on time performance points using the government structure',

    }, {

        name: 'overdue',
        label: 'Minimise Final Overdue',
        description: 'Minimise the total number of overdue patients on the waiting list at the end of the time horizon',

    }, {

        name: 'totalList',
        label: 'Minimise Final Waiting List',
        description: 'Minimise the total number of people on the waiting list at the end of the time horizon',

    }, {

        name: 'cat1',
        label: 'Category 1',
        description: 'Minimise the number of days of category 1 patients waiting overdue during the entire time horizon',

    }, {

        name: 'duration',
        label: 'Surgery Time Left',
        description: 'Minimise the amount of surgery time left on the waiting list at the end of the time horizon, ' +
                'measured in how many weeks of full time surgery would be required to manage that much surgery time',

    }, {

        name: 'beds',
        label: 'Ward Nights Left',
        description: 'Minimise the number of required nights of stay on the ward left on the waiting list at the ' +
                'end of the time horizon, measured in how many weeks of complete bed usage would be required to ' +
                'manage that many nights',

    }, {

        name: 'imbalance',
        label: 'Workload imbalance',
        description: 'Minimise the difference between the remaining surgery time and the remaining ward nights, measured in consistent units of weeks',

    },
    ],

    parameters: [{

        name: 'inputWeightOverdue',
        label: 'Overdue Waiting List',
        description: 'Penalise the number of overdue patients still on the waiting list at the end of period. In general, there is a higher penalty for the overdue patients than on-time.',
        type: 'INT',
        range: [0, 10],
        value: 0,
        objective: 'overdue',

    }, {

        name: 'inputWeightOntime',
        label: 'On-time Waiting List',
        description: 'Penalise the number of patients left on the waiting list and still on-time at the end of period.',
        type: 'INT',
        range: [0, 10],
        value: 0,

    }, {

        name: 'inputExtraNormalBeds',
        label: 'Extra Ward Beds',
        description: 'The number of additional ward beds per day',
        type: 'INT',
        range: [0, 500],
        value: 0,

    }, {

        name: 'inputExtraIcuBeds',
        label: 'Extra ICU Beds',
        description: 'The number of additional ICU beds per day',
        type: 'INT',
        range: [0, 50],
        value: 0,

    }, {

        name: 'inputPerformancePointBonus',
        label: 'Performance Point Bonus',
        description: 'The bonus for each point in performance',
        type: 'INT',
        range: [0, 100000],
        value: 0,
        objective: 'points',

    }, {

        name: 'inputLastPeriodOverduePatientPenalty',
        label: 'Last Period Overdue Patient Penalty',
        description: 'The penalty for the overdue patient on the last period',
        type: 'INT',
        range: [0, 100],
        value: 0,

    }, {

        name: 'inputPerPeriodWaitlistPatientPenalty',
        label: 'Per Period Waitlist Patient Penalty',
        description: 'The penalty for the patients on the waiting list for each period.',
        type: 'INT',
        range: [0, 10],
        value: 0,
        objective: 'totalList',

    }, {

        name: 'inputUrgencyCategory1PatientPenalty',
        label: 'Urgency Category 1 Overdue Patient Penalty',
        description: 'The penalty for initial overdue patients for urgency category 1',
        type: 'INT',
        range: [0, 100000],
        value: 0,
        objective: 'cat1',
    }],

    outputMappings: [{

        mappingType: 'complex',
        fileName: 'beds.csv',
        sources: [{

            solutionKey: 'bedAvailRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['available'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            valueKeys: ['value'],
            row: { labels: ['available'] },
            value: { keys: ['value'] },

        }, {

            solutionKey: 'bedTotalRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['totalUsed'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            valueKeys: ['value'],
            row: { labels: ['totalUsed'] },
            value: { keys: ['value'] },
        },
        {
            solutionKey: 'bedRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowEntryKeys: ['name'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            valueKeys: ['value'],
            row: { entryKeys: ['name'] },
            value: { keys: ['value'] },
        },
        ],

    }, {

        mappingType: 'complex',
        fileName: 'icu.csv',
        sources: [{

            solutionKey: 'icuAvailRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['available'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            valueKeys: ['value'],
            row: { labels: ['available'] },
            value: { keys: ['value'] },

        }, {

            solutionKey: 'icuTotalRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['totalUsed'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            valueKeys: ['value'],
            row: { labels: ['totalUsed'] },
            value: { keys: ['value'] },

        }, {

            solutionKey: 'icuRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowEntryKeys: ['name'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            valueKeys: ['value'],
            row: { entryKeys: ['name'] },
            value: { keys: ['value'] },
        }],

    }, {

        mappingType: 'complex',
        fileName: 'patients.csv',
        sources: [{

            solutionKey: 'patientsRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['Waiting'],
            rowEntryKeys: ['waiting'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            row: { entryKeys: ['waiting'], labels: ['Waiting'] },

        }, {

            solutionKey: 'patientsRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['Incoming'],
            rowEntryKeys: ['incoming'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            row: { entryKeys: ['incoming'], labels: ['Incoming'] },

        }, {

            solutionKey: 'patientsRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['Treated'],
            rowEntryKeys: ['treated'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            row: { entryKeys: ['treated'], labels: ['Treated'] },

        }, {

            solutionKey: 'patientsRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['Still Waiting'],
            rowEntryKeys: ['stillWaiting'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            row: { entryKeys: ['stillWaiting'], labels: ['Still Waiting'] },

        }, {

            solutionKey: 'patientsRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['Overdue Incoming'],
            rowEntryKeys: ['incomingOverdue'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            row: { entryKeys: ['incomingOverdue'], labels: ['Overdue Incoming'] },

        }, {

            solutionKey: 'patientsRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['Overdue Waiting'],
            rowEntryKeys: ['waitingOverdue'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            row: { entryKeys: ['waitingOverdue'], labels: ['Overdue Waiting'] },

        }, {

            solutionKey: 'patientsRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['Overdue Treated'],
            rowEntryKeys: ['treatedOverdue'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            row: { entryKeys: ['treatedOverdue'], labels: ['Overdue Treated'] },

        }, {

            solutionKey: 'patientsRes',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['Overdue Still Waiting'],
            rowEntryKeys: ['stillWaitingOverdue'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            row: { entryKeys: ['stillWaitingOverdue'], labels: ['Overdue Still Waiting'] },
        }],

    }, {


        mappingType: 'complex',
        fileName: 'sessions.csv',
        sources: [{

            solutionKey: 'sessionsTheatres',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['total theatres'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            valueKeys: ['value'],
            row: { labels: ['total theatres'] },
            value: { keys: ['value'] },

        }, {

            solutionKey: 'sessionsTotal',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['total sessions'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            valueKeys: ['value'],
            row: { labels: ['total sessions'] },
            value: { keys: ['value'] },

        }, {

            solutionKey: 'sessions',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowEntryKeys: ['department'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            valueKeys: ['value'],
            row: { entryKeys: ['department'] },
            value: { keys: ['value'] },
        }],

    }, {

        mappingType: 'complex',
        fileName: 'surgeries.csv',
        sources: [{

            solutionKey: 'surgeryResults',
            column: { entryKeys: ['period'], labels: ['period'] },
            rowLabels: ['Surgery Type', 'Urgency Category'],
            rowEntryKeys: ['surgery', 'cat'],
            columnLabels: ['period'],
            columnEntryKeys: ['period'],
            valueKeys: ['value'],
            row: { entryKeys: ['surgery', 'cat'], labels: ['Surgery Type', 'Urgency Category'] },
            value: { keys: ['value'] },
        }],

    }, {

        mappingType: 'key-to-column',
        fileName: 'targets.csv',
        sources: [{

            solutionKey: 'targetsRes',

        }],

    }, {

        mappingType: 'complex-append',
        fileName: 'waitlist.csv',
        sources: [{

            solutionKey: 'waitListTotal',
            column: { entryKeys: ['period'], labels: ['total period'] },
            rowLabels: ['Unit Code', 'Surgery Type', 'Urgency Category'],
            rowEntryKeys: ['unitCode', 'surgery', 'cat'],
            columnLabels: ['total period'],
            columnEntryKeys: ['period'],
            valueKeys: ['value'],
            row: {
                entryKeys: ['unitCode', 'surgery', 'cat'],
                labels: ['Unit Code', 'Surgery Type', 'Urgency Category'],
            },
            value: { keys: ['value'] },

        }, {

            solutionKey: 'waitListOverdue',
            column: { entryKeys: ['period'], labels: ['overdue period'] },
            rowLabels: ['Unit Code', 'Surgery Type', 'Urgency Category'],
            rowEntryKeys: ['unitCode', 'surgery', 'cat'],
            columnLabels: ['overdue period'],
            columnEntryKeys: ['period'],
            valueKeys: ['value'],
            row: {
                entryKeys: ['unitCode', 'surgery', 'cat'],
                labels: ['Unit Code', 'Surgery Type', 'Urgency Category'],
            },
            value: { keys: ['value'] },
        }],

    }, {

        mappingType: 'transformer',
        transformer: 'ScheduleTransformer',
        fileName: 'schedule.json',
        sources: [{

            solutionKey: 'schedule',
        }],

    }, {

        mappingType: 'json-category',
        fileName: 'schedule-surgery-types.json',
        sources: [{

            solutionKey: 'schedule_surgery_types',
            rowEntryKeys: ['clusterName'],
            valueKeys: ['medicalUnitID', 'urgencyCategory', 'duration', 'lengthOfStay', 'icuRequirementProbability'],
            row: { entryKeys: ['clusterName'] },
            value: { keys: ['medicalUnitID', 'urgencyCategory', 'duration', 'lengthOfStay', 'icuRequirementProbability'] },
        }],
    }],

    attachments: [{
        name: 'model.ops',
        contentLength: 196,
        contentType: 'text/plain',
    }, {
        name: 'script.mod',
        contentLength: 47597,
        contentType: 'application/octet-stream',
    }],


}];


/**
 * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * * ********************
 * ********************
 */


runsMock = [{
    id: '4cb50f88d24aff6d238d401e2900e8e1',
    revision: '27-4094e1706b14f49fc33e0de79f36b191',

    templateId: '4cb50f88d24aff6d238d401e29009d5e',
    modelId: '4cb50f88d24aff6d238d401e29008aaf',

    datasetId: '4cb50f88d24aff6d238d401e29009121',
    jobId: 'job.501202946.1453520425590.68',
    resultId: '4cb50f88d24aff6d238d401e29009122',

    label: 'This is a test execution',
    description: 'This is an example of a completed run. The execution has been successful, there is an associated ' +
            '"jobId", ans "resultId", the status is represented by "status". The final value of the gap is in ' +
            '"finalGap". The information about execution time is in "runTime", which shows the time in milliseconds ' +
            '(I believe), the "startTime" contains the unix epoc time of the start time. The value of "maxRunTime" ' +
            'is what the user can setup as execution time, as well as the value of "minGap".',

    status: 'COMPLETED',
    jobStatus: 'COMPLETED',
    solveStatus: 'OPTIMAL_SOLUTION',

    minGap: 0.005,
    maxRunTime: 10,

    finalGap: 0.007662,
    runTime: 642169,

    startTime: 1453542103717,


    parameters: [{

        name: 'inputExtraNormalBeds',
        value: 0,

    }, {

        name: 'inputWeightOntime',
        value: 0,

    }, {

        name: 'inputWeightOverdue',
        value: 0,

    }, {

        name: 'inputExtraIcuBeds',
        value: 0,

    }, {

        name: 'inputExtraNormalBeds',
        value: 0,
    }],

    attachments: {

        'solution.json': { contentLength: 1393254, contentType: 'application/json' },
        'icu.csv': { contentLength: 1654, contentType: 'text/csv' },
        'targets.csv': { contentLength: 200, contentType: 'text/csv' },
        'beds.csv': { contentLength: 1243, contentType: 'text/csv' },
        'surgeries.csv': { contentLength: 9296, contentType: 'text/csv' },
        'schedule.json': { contentLength: 61837, contentType: 'application/json; charset=UTF-8' },
        'sessions.csv': { contentLength: 1266, contentType: 'text/csv' },
        'log.txt': { contentLength: 87273, contentType: 'plain/text' },
        'waitlist.csv': { contentLength: 19444, contentType: 'text/csv' },
        'schedule-surgery-types.json': { contentLength: 15876, contentType: 'application/json; charset=UTF-8' },
        'patients.csv': { contentLength: 1177, contentType: 'text/csv' },
    },

}, {
    id: '4cb50f88d24aff6d238d401e2900e8e2',
    revision: '27-4094e1706b14f49fc33e0de79f36b191',

    templateId: '4cb50f88d24aff6d238d401e29009d5e',
    modelId: '4cb50f88d24aff6d238d401e29008aaf',

    datasetId: '4cb50f88d24aff6d238d401e29009121',


    label: 'This is a test execution',
    description: "This is an example of a queued run. As you can see there are no attachments, there is no information about the 'jobId', 'jobStatus', 'resultId', or 'solveStatus'.",

    status: 'QUEUED',

    minGap: 0.005,
    maxRunTime: 10,

    finalGap: 1.0,
    runTime: 0,

    startTime: 1453542103717,


    parameters: [{

        name: 'inputExtraNormalBeds',
        value: 0,

    }, {

        name: 'inputWeightOntime',
        value: 0,

    }, {

        name: 'inputWeightOverdue',
        value: 0,

    }, {

        name: 'inputExtraIcuBeds',
        value: 0,

    }, {

        name: 'inputExtraNormalBeds',
        value: 0,
    }],

}, {

    id: '4cb50f88d24aff6d238d401e2900e8e3',
    revision: '27-4094e1706b14f49fc33e0de79f36b191',

    templateId: '4cb50f88d24aff6d238d401e29009d5e',
    modelId: '4cb50f88d24aff6d238d401e29008aaf',

    datasetId: '4cb50f88d24aff6d238d401e29009121',
    resultId: '4cb50f88d24aff6d238d401e29009123',


    label: 'This is a test execution',
    description: "This is an example of a queued run. As you can see there are no attachments, there is no information about the 'jobId', 'jobStatus', 'resultId', or 'solveStatus'.",

    status: 'PROCESSING',

    minGap: 0.005,
    maxRunTime: 10,

    finalGap: 1.0,
    runTime: 0,

    startTime: 1453542103717,

    jobId: 'job.501202946.1460712007215.95',
    jobState: 'RUNNING',
    solveStatus: 'PENDING',

    parameters: [{

        name: 'inputExtraNormalBeds',
        value: 0,

    }, {

        name: 'inputWeightOntime',
        value: 0,

    }, {

        name: 'inputWeightOverdue',
        value: 0,

    }, {

        name: 'inputExtraIcuBeds',
        value: 0,

    }, {

        name: 'inputExtraNormalBeds',
        value: 0,
    }],
}, {

    id: '4cb50f88d24aff6d238d401e2900e8e4',
    revision: '27-4094e1706b14f49fc33e0de79f36b191',

    templateId: '4cb50f88d24aff6d238d401e29009d5e',
    modelId: '4cb50f88d24aff6d238d401e29008aaf',

    datasetId: '4cb50f88d24aff6d238d401e29009121',
    resultId: '4cb50f88d24aff6d238d401e29009124',


    label: 'This is a test execution',
    description: 'This is an example of a failed run. What matters to you is the value of the "status" field as well ' +
            'as the value of "resultId" which will contain additional information about the failure.',

    status: 'FAILED',

    minGap: 0.005,
    maxRunTime: 10,

    finalGap: 1.0,
    runTime: 0,

    startTime: 1453542103717,

    jobId: 'job.501202946.1460712007215.95',
    jobState: 'FAILED',
    solveStatus: 'PENDING',

    parameters: [{

        name: 'inputExtraNormalBeds',
        value: 0,

    }, {

        name: 'inputWeightOntime',
        value: 0,

    }, {

        name: 'inputWeightOverdue',
        value: 0,

    }, {

        name: 'inputExtraIcuBeds',
        value: 0,

    }, {

        name: 'inputExtraNormalBeds',
        value: 0,
    }],
}];


