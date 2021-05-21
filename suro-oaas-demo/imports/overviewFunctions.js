import moment from 'moment'

const overview = {

    resetModalWindow() {
        gap.set(99);
        runTime.set(10);
        $('.newRunLabelTune').val('');
        $('.rangeTime').val(10);
        $('.rangeOptimality').val(99);
        $('.newRunLabelMax').val('');
        $('.extraBedsInput').val(0);
        $('.extraIcuInput').val(0);
        $('#tunePerformance .modalInputText').each((index, item) => {
            $(item).val(1)
        });
        $('#minWaitListCB').prop('checked', true);
        $('#minOverdueCB').prop('checked', false);
    },


    renderBasePlanSessionChart() {

        const solutionHandler = Template.overview.solutionHandler.get();
        const columnData = {}

        if (solutionHandler == null) {
            return;
        }

        Object.keys(solutionHandler.sessions).forEach((period) => {

            // retrieve entry
            const entry = solutionHandler.sessions[period];

            // deeper analysis to update the column data
            Object.keys(entry).forEach((depName) => {

                // make sure the entry exists
                if (columnData[depName] === undefined) {
                    columnData[depName] = {
                        department: depName,
                        baseSessions: 0,
                    }
                }

                // store number of sessions
                const currentDep = columnData[depName];
                currentDep.baseSessions += entry[depName].baseSessions;
                columnData[depName] = currentDep;

            });
        });

        // compile columns data for chart
        const categories = [];
        const baseSessions = ['Sessions'];
        Object.keys(columnData).forEach((depName) => {
            categories.push(depName);
            baseSessions.push(columnData[depName].baseSessions);
        });

        // make sure to render chart
        setTimeout(() => {
            const chartData = {
                bindto: '#base-plan-session-chart',
                size: {
                    height: 230,
                },
                data: {
                    columns: [baseSessions],
                    type: 'bar',
                },
                color: {
                    pattern: ['#79D2F2'],
                },
                axis: {
                    x: {
                        type: 'category',
                        categories,
                        tick: {
                            rotate: -40,
                            multiline: false,
                        },
                        height: 80,
                    },
                },
                legend: {
                    hide: true,
                    show: false,
                },
                grid: {
                    y: {
                        show: true,
                    },
                    x: {
                        show: true,
                    },
                },
                bar: {
                    width: {
                        ratio: 0.5,
                    },
                },
            };
            const chart = c3.generate(chartData);
            Template.sessionTab.currentChartData.set(chart.data());
        }, 500);
    },

    submitMax() {
        let minOnTime = 1;
        let minOverdue = 0;
        const minWaitListCB = $('#minWaitListCB');
        const minOverdueCB = $('#minOverdueCB');
        if (minWaitListCB.is(':checked')) {
            if (minOverdueCB.is(':checked')) {
                minOverdue = 2;
            } else {
                minOverdue = 1;
            }

        } else if (minOverdueCB.is(':checked')) {
            minOnTime = 0;
            minOverdue = 1;
        }

        const extraIcu = $('.extraIcuInput').val();
        const extraBeds = $('.extraBedsInput').val();

        const maxRunTime = $('.rangeTime').val();
        const minGap = (100 - parseInt($('#minGap').val(), 10)) / 100;

        let tLabel = $('.newRunLabelMax').val();
        if (tLabel === '') {
            tLabel = moment(Date.now()).format('YYYY-MM-DD @ HH:mm');
        }
        // const tLabel = moment(Date.now()).format('YYYY-MM-DD @ HH:mm');
        const tParameters = [];
        let object = { name: 'inputExtraNormalBeds', value: parseInt(extraBeds, 10) };


        tParameters.push(object);

        //  object={name:inputExtraNormalBeds,value:parseInt(extraBeds)};
        object = { name: 'inputExtraIcuBeds', value: parseInt(extraIcu, 10) };
        tParameters.push(object);

        object = { name: 'inputWeightOverdue', value: minOverdue };
        tParameters.push(object);

        object = { name: 'inputWeightOntime', value: minOnTime }
        tParameters.push(object);


        currentStrategy.set(strategies.findOne({ label: 'Maximize the performance of the hospital' }))
        console.log(currentStrategy.get());


        const submission = {
            templateId: currentStrategy.get().id,
            modelId: currentModel.get().id,
            parameters: tParameters,
            minGap,
            maxRunTime: parseInt(maxRunTime, 10),
            label: tLabel,
        };

        // console.log(submission);


        Meteor.call('submitRun', submission, (err, resp) => {
            if (!err) {
                console.log(resp);
                sAlert.success('Succesfully submitted to DO Cloud');
                gap.set(99);
                runTime.set(10);
                currentRun.set(runs.findOne({ description: '%DefaultPlan%' }))
                const tStrategy = strategies.findOne({ label: 'Flexible (free form)' });
                currentStrategy.set(tStrategy);

            } else {
                sAlert.warning('Error Deploying', err);
                console.log(err);
            }
        });
        $('.maxPerformance').leanModal('close');
    },

    submitFineTune() {
        let test = true;
        const tParameters = [];
        $('#tunePerformance input').not('.notSelected').each((index, item) => {
            const min = $(item).attr('min');
            const max = $(item).attr('max');
            const tParameter = {
                name: $(item).attr('id'),
                value: parseInt($(item).val(), 10),
            };

            const testVal = parseInt(tParameter.value, 10);
            // console.log(testVal);
            if (isNaN(testVal)) {
                test = false;
                sAlert.error('There is a missing value, please fix before proceeding')
            }

            if (parseInt(tParameter.value, 10) < min || parseInt(tParameter.value, 10) > max) {
                test = false;
                sAlert.error('Value out of range,  fix before proceeding')
            }
            tParameters.push(tParameter);

        });
        if (test) {
            let tLabel = $('.newRunLabelTune').val();
            if (tLabel === '') {
                tLabel = moment(Date.now()).format('YYYY-MM-DD @ HH:mm');
            }
            // const tLabel = moment(Date.now()).format('YYYY-MM-DD @ HH:mm');

            const minGap = (100 - parseInt($('#minGap').val(), 10)) / 100;
            const submission = {
                templateId: currentStrategy.get().id,
                modelId: currentModel.get().id,
                parameters: tParameters,
                minGap,
                maxRunTime: parseInt($('#runTime').val(), 10),
                label: tLabel,
            };
            sAlert.info('Submitting');
            console.log(submission);
            Meteor.call('submitRun', submission, (err, resp) => {
                if (!err) {
                    console.log(resp);
                    sAlert.success('Succesfully submitted to DO Cloud');
                    gap.set(99);
                    runTime.set(10);

                } else {
                    sAlert.warning('Error Deploying', err);
                    console.log(err);
                }
            });

        }

    },

    goToDefault() {
        const run = runs.findOne({ description: '%DefaultPlan%' });
        console.log('run', run)
        if (run != null && run.status === 'COMPLETED') {

            runTitle.set(run.label);
            runDate.set(moment(run.startTime).format('YYYY-MM-DD'));

            currentRun.set(runs.findOne({ id: run.id }));

            Meteor.call('loadRunSolution', run.id, (err, data) => {
                if (err) {
                    currentSolution.set();
                } else {
                    currentSolution.set(data);
                    Router.go('dashboard');
                }
            });

        } else {
            sAlert.warning('Run Processing, please wait for completion');
        }
    },

}


export default overview;
