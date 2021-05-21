Template.patientsTab.helpers({

    /**
     * Retrieves the list of departments from the currently loaded hospital.
     * @returns {*} - a list of departments
     */
    hospitalDepartments () {
        const hospital = currentHospital.get();
        if (hospital !== undefined) {
            return hospital.departments;
        }
        return [];
    },

});

Template.patientsTab.events({

    /**
     * Switch between waitlist and treated patients
     * @param {Object} e - the click event on the toggle
     */
    'click .tabsListPatients a[data-view]': (e) => {
        const view = $(e.target).closest('[data-view]').attr('data-view');
        const filter = currentPatientFilter.get();
        filter.view = view;
        currentPatientFilter.set(filter);
    },


    /**
     * Select a patient status filter (on time / overdue)
     * @param {Object} e - the click even on the toggle
     */
    'change input[type="checkbox"][data-status]': (e) => {
        const newStatus = [];
        $('input[type="checkbox"][data-status]').each((index, item) => {
            if ($(item).is(':checked')) {
                newStatus.push($(item).attr('data-status'));
            }
        });
        const status = $(e.target).closest('[data-status]').attr('data-status');

        const filter = currentPatientFilter.get();
        filter.status = newStatus;
        currentPatientFilter.set(filter);
    },


    'change input[type="checkbox"][data-department-name]': () => {
        const newDepartmentList = [];

        $('input[type="checkbox"][data-department-name]:checked').each((index, item) => {
            newDepartmentList.push($(item).attr('data-department-name'));
        });

        const filter = currentPatientFilter.get();
        filter.department = newDepartmentList;
        currentPatientFilter.set(filter);
    },

    'change input[type="checkbox"][data-category-id]': () => {
        const newCategoies = [];
        $('input[type="checkbox"][data-category-id]:checked').each((index, item) => {
            newCategoies.push(parseInt($(item).attr('data-category-id'), 10));
        });

        const filter = currentPatientFilter.get();
        filter.category = newCategoies;
        currentPatientFilter.set(filter);
    },

    'change input[type="radio"][data-explode]': () => {
        const newExplode = $('input[type="radio"][data-explode]:checked').attr('data-explode');

        const filter = currentPatientFilter.get();
        filter.explode = newExplode;
        if (Template.patientsChart.chartUpdater !== undefined) {
            Template.patientsChart.chartUpdater.resetChart();
        }

        currentPatientFilter.set(filter);

    },


});

Template.patientsTab.rendered = function () {
    $('.categoryDropdown').dropdown({
        inDuration: 300,
        outDuration: 225,
        constrain_width: true, // Does not change width of dropdown to that of the activator
        hover: true, // Activate on hover
        gutter: 0, // Spacing from edge
        belowOrigin: true, // Displays dropdown below the button
        alignment: 'left', // Displays dropdown with edge aligned to the left of button
    });
    $('.departmentDropdown').dropdown({
        inDuration: 300,
        outDuration: 225,
        constrain_width: true, // Does not change width of dropdown to that of the activator
        hover: true, // Activate on hover
        gutter: 0, // Spacing from edge
        belowOrigin: true, // Displays dropdown below the button
        alignment: 'left', // Displays dropdown with edge aligned to the left of button
    });


    $('ul.tabsListPatients').tabs();


};
