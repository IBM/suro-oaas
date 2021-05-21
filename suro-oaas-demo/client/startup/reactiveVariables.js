/**
 * This variable is used to retrieve the current selected run, it is used to populate
 * the ui in the summary page and also the charts page
 * @type {ReactiveVar}
 */
import moment from 'moment'

currentRun = new ReactiveVar();

navBarRenderd = new ReactiveVar(false);
/**
 *
 * @type {ReactiveVar}
 */
currentPatientFilter = new ReactiveVar();

currentBedsFilter = new ReactiveVar();

/**
 * Holding the solution of the currently selected run. If the run is not finished the variable will be null.
 * @type {ReactiveVar}
 */
currentSolution = new ReactiveVar();

/**
 * This identifies the current strategy basd on the selected template, it is used
 * to populate the "Run SET UP" page
 * @type {ReactiveVar}
 */
currentStrategy = new ReactiveVar();

currentModel = new ReactiveVar();

currentHospital = new ReactiveVar();


// this variable is used in the setup screen to keep track of whether or not to show the
// screen

showSetup = new ReactiveVar(false);

runTime = new ReactiveVar(10);
gap = new ReactiveVar(99);

runTitle = new ReactiveVar('Please Select a Scenario');
runDate = new ReactiveVar(moment(Date.now()).format('YYYY-MM-DD'));

Meteor.startup(() => {


    /* Meteor.call('getLastRun', (err, data) => {
        if (!err) {
            currentRun.set(data);
            runTitle.set(`Run : ${data.label}`);
            Meteor.call('loadRunSolution', data.id, (err2, solution) => {
                if (!err2) {
                    currentSolution.set(solution);
                }
            });
        }
    });*/

    Meteor.call('getCurrentHospital', (err, data) => {
        if (!err) {
            currentHospital.set(data);

            const patientFilter = {
                view: 'treated',
                status: ['overdue', 'onTime'],
                department: [],
                category: [],
                explode: '',
            };

            currentPatientFilter.set(patientFilter);

            const bedsFilter = {
                view: 'beds',
                departments: [],
                bedTypes: ['beds'],
                aggregate: 'day',
                collapse: false,
            }

            currentBedsFilter.set(bedsFilter);
        }
    });


    let count = 0;
    const query = runs.find();
    const handle = query.observeChanges({
        added (document) {
            //  console.log('Added document into runs :',document);
        },
        changed (document) {
            const doc = runs.findOne(document);
            if (doc.status === 'COMPLETED') {

                sAlert.info(`Run ${doc.label} completed`);

            } else if (doc.status === 'FAILED') {

                sAlert.warning(`Run ${doc.label} failed`);

            } else if (doc.status === 'ABORTED') {

                sAlert.warning(`Run ${doc.label} stopped`);
            }

            console.log('Run Completed runs :', doc);
        },
        removed () {
            count -= 1;
            console.log(`Lost one. We're now down to ${count} runs.`);
        },
    });
});
