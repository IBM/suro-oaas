import moment from 'moment'

Template.genericSetupBox.helpers({
    myStrategy () {
        /* console.log('my strategy');
         var tStrategy=strategies.findOne({id:'4cb50f88d24aff6d238d401e29009d5e'});
         console.log(tStrategy);
         */
        return currentStrategy.get();
    },

    displaySetup () {
        return showSetup.get();
    },
    optimalityRange () {
        //   return
    },
    runTime () {
        return runTime.get();
    },
    gap () {
        return gap.get()
    },
});

Template.genericSetupBox.events({
    'change .rangeTime': (event) => {
        runTime.set(parseInt($(event.target).val(), 10));
    },

    'change .rangeOptimality': (event) => {
        gap.set($(event.target).val());
    },


    'click .btn-submission': () => {
        let test = true;

        const tParameters = [];
        $('#view-genericSetupBox input').not('.notSelected').each((index, item) => {
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
            let tLabel = $('#newrRunLabel').val();
            if (tLabel === '') {
                tLabel = moment(Date.now()).format('YYYY-MM-DD @ HH:mm');
            }
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
                    $('ul.homeTabs').tabs('select_tab', 'summary');
                    currentStrategy.set('');

                } else {
                    sAlert.warning('Error Deploying', err);
                    console.log(err);
                }
            });

        }

    },
});

Template.genericSetupBox.rendered = function () {

};
