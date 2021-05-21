/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 * This file describes the schema of the old CouchDB database schema
 */


/**
 * Sub-Schema for the complex field "criteria" in a strategy document
 * @type {{name: {type: Function}, type: {type: Function, allowedValues: string[]}, defaultValue: {type: Function}}}
 */
var criteriaSchema = {
    name: {
        type: String
    },
    type: {
        type: String,
        // currently only number is used in the example data
        allowedValues: ['number', 'string', 'boolean']
    },
    defaultValue: {
        // will be parsed into the corresponding format
        type: String
    }
}

/**
 * Sub-Schema used inside the "_attachments" of a run document.
 * @type {{content_type: {type: Function, allowedValues: string[]}, revpos: {type: Function}, digest: {type: Function}, length: {type: Function}, stub: {type: Function}}}
 */
var fileSchema = {
    content_type: {
        type: String,
        // TODO: add remaining values
        allowedValues: ['plain/text', 'application/json', '...']
    },
    revpos: {
        type: Number
    },
    digest: {
        type: String
    },
    length: {
        type: Number
    },
    stub: {
        type: Boolean
    }
}

/**
 * Sub-Schema for the "_attachments" field of a run document
 * @type {{<filename>: {type: {content_type: {type: Function, allowedValues: string[]}, revpos: {type: Function}, digest: {type: Function}, length: {type: Function}, stub: {type: Function}}}}}
 */
var runAttachmentSchema = {
    // <filename> examples for the keys are: "log.txt", "solution.json", etc.
    "<filename>": {
        type: fileSchema
    }
}

/**
 * Sub-Schema for the "data" field of a run document
 * @type {{maxRuntime: {type: Function, optional: boolean}, optimalityBoundary: {type: Function, optional: boolean}, inputExtraNormalBeds: {type: Function, optional: boolean}, inputWeightOntime: {type: Function, optional: boolean}, inputExtraIcuBeds: {type: Function, optional: boolean}, inputWeightOverdue: {type: Function, optional: boolean}, inputPerformancePointBonus: {type: Function, optional: boolean}, inputPerPeriodWaitlistPatientPenalty: {type: Function, optional: boolean}, inputLastPeriodOverduePatientPenalty: {type: Function, optional: boolean}}}
 */
var runConstraintSchema = {
    // common fields
    maxRuntime: {
        type: String,
        optional: true
    },
    optimalityBoundary: {
        type: String,
        optional: true
    },

    // depends on the strategy
    inputExtraNormalBeds: {
        type: String,
        optional: true
    },
    inputWeightOntime: {
        type: String,
        optional: true
    },
    inputExtraIcuBeds: {
        type: String,
        optional: true
    },
    inputWeightOverdue: {
        type: String,
        optional: true
    },
    inputPerformancePointBonus: {
        type: String,
        optional: true
    },
    inputPerPeriodWaitlistPatientPenalty: {
        type: String,
        optional: true
    },
    inputLastPeriodOverduePatientPenalty: {
        type: String,
        optional: true
    }
}

/**
 * Sub-Schema for the "entries" field of an optimization result document
 * @type {{time: {type: Function}, node: {type: Function}, otherCheck: {type: Function}, nodesLeft: {type: Function}, objective: {type: Object, optional: boolean}, iinf: {type: Object, optional: boolean}, bestInteger: {type: Function}, bestBound: {type: Function}, totalIterations: {type: Object, optional: boolean}, gap: {type: Function}, rawLine: {type: Object, optional: boolean}, isSolution: {type: Function}}}
 */
var entrySchema = {
    time: {
        // a timestamp
        type: Number
    },
    node: {
        // an integer
        type: Number
    },
    otherCheck: {
        type: Boolean
    },
    nodesLeft: {
        // an integer
        type: Number
    },
    objective: {
        // can be null or a double
        type: Number,
        optional: true
    },
    iinf: {
        // can be null or an integer
        type: Number,
        optional: true
    },
    bestInteger: {
        // an integer
        type: Number
    },
    bestBound: {
        // a double value
        type: Number
    },
    totalIterations: {
        // can be null or an int
        type: Number,
        optional: true
    },
    gap: {
        // a double value
        type: Number
    },
    rawLine: {
        // can be null
        type: String,
        optional: true
    },
    isSolution: {
        type: Boolean
    }
}


/**
 * Main Schema for any document in CouchDB. The cdb<Something> parameters determine the type of the document
 * @type {{_id: {type: Function}, _rev: {type: Function}, cdbOptimResult: {type: Function, optional: boolean, allowedValues: string[]}, cdbStrategy: {type: Function, optional: boolean, allowedValues: string[]}, cdbRun: {type: Function, optional: boolean, allowedValues: string[]}, title: {type: Function, optional: boolean}, description: {type: Function, optional: boolean}, assumptions: {type: *[], optional: boolean}, criteria: {type: *[], optional: boolean}, isRunning: {type: Function, optional: boolean}, resultsPathRoot: {type: Function, optional: boolean}, editorTemplate: {type: Function, optional: boolean}, running: {type: Function, optional: boolean}, approxProcessingDuration: {type: Function, optional: boolean}, strategyId: {type: Function, optional: boolean}, jobId: {type: Function, optional: boolean}, state: {type: Function, optional: boolean, allowedValues: string[]}, dateRun: {type: Function, optional: boolean}, solveStatus: {type: Function, optional: boolean, allowedValues: string[]}, runTime: {type: Function, optional: boolean}, finalGap: {type: Function, optional: boolean}, hasSolution: {type: Function, optional: boolean}, finished: {type: Function, optional: boolean}, data: {type: {maxRuntime: {type?: Function, : boolean}, optimalityBoundary: {type?: Function, : boolean}, inputExtraNormalBeds: {type?: Function, : boolean}, inputWeightOntime: {type?: Function, : boolean}, inputExtraIcuBeds: {type?: Function, : boolean}, inputWeightOverdue: {type?: Function, : boolean}, inputPerformancePointBonus: {type?: Function, : boolean}, inputPerPeriodWaitlistPatientPenalty: {type?: Function, : boolean}, inputLastPeriodOverduePatientPenalty: {type?: Function, : boolean}}, optional: boolean}, _attachments: {type: *[], optional: boolean}, runId: {type: Function, optional: boolean}, entries: {type: *[], optional: boolean}, bestBound: {type: Function, optional: boolean}, bestInteger: {type: Function, optional: boolean}, gap: {type: Function, optional: boolean}}}
 */
var schema = {
    // common CouchDB fields
    _id: {
        type: String
    },
    _rev: {
        type: String
    },

    // depending on the document type, pick only one of the following 3 fields:
    cdbOptimResult: {
        type: String,
        optional: true,
        allowedValues: ['OptimResult']
    },
    cdbStrategy: {
        type: String,
        optional: true,
        allowedValues: ['Strategy']
    },
    cdbRun: {
        type: String,
        optional: true,
        allowedValues: ['Run']
    },

    // Strategy fields, all fields mandatory for Strategy records
    title: {
        type: String,
        optional: true
    },
    description: {
        type: String,
        optional: true
    },
    assumptions: {
        // for Strategy records => empty array, no values available, so unknown data type
        type: [Object],
        optional: true
    },
    criteria: {
        type: [criteriaSchema],
        optional: true
    },
    isRunning: {
        // potential duplicate of "running"
        type: Boolean,
        optional: true
    },
    resultsPathRoot: {
        // e.g. "resources/data/optimizationResults/strategy3/"
        type: String,
        optional: true
    },
    editorTemplate: {
        // e.g. optimizationStrategyConstraints3
        type: String,
        optional: true
    },
    running: {
        type: Boolean,
        optional: true
    },
    approxProcessingDuration: {
        // the number of minutes it will approximately take to calculate the optimum
        type: Number,
        optional: true
    },

    // Run fields
    strategyId: {
        // a number referencing the strategy ID ("1", "2", "3" or "4", but could be a standard _id)
        type: String,
        optional: true
    },
    jobId: {
        type: String,
        optional: true
    },
    state: {
        type: String,
        optional: true,
        // TODO: add missing values
        allowedValues: ['FAILED', "PROCESSED", "..."]
    },
    dateRun: {
        // time stamp when the run was started
        type: Number,
        optional: true
    },
    solveStatus: {
        type: String,
        optional: true,
        // TODO: add missing values
        allowedValues: ['UNKNOWN', 'OPTIMAL_SOLUTION', "..."]
    },
    runTime: {
        // the number of milliseconds the run took (or 0, if the run is not finished)
        type: Number,
        optional: true
    },
    finalGap: {
        // a double value
        type: Number,
        optional: true
    },
    hasSolution: {
        type: Boolean,
        optional: true
    },
    finished: {
        // set to true on abort and on finish
        type: Boolean,
        optional: true
    },
    data: {
        type: runConstraintSchema,
        optional: true
    },
    _attachments: {
        // not set if the run is not finished, but aborted
        type: [runAttachmentSchema],
        optional: true
    },

    // OptimResult fields
    runId: {
        type: String,
        optional: true
    },
    entries: {
        type: [entrySchema],
        optional: true
    },
    bestBound: {
        type: Number,
        optional: true
    },
    bestInteger: {
        type: Number,
        optional: true
    },
    gap: {
        type: Number,
        optional: true
    }
}