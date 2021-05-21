/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */
Template.departments.helpers({
    departmentSpecialist: (specList, depName) => {

        const result = [];
        specList.forEach((spec) => {
            if (spec.department === depName) {
                result.push(spec);
            }
        });

        return result;

    },
});

Template.departments.events({
    // new add department row
    'click #dep-add-button': () => {
        $('#dep-new-container').show();
    },

    // add new department
    'click #dep-new-button': (e) => {
        if (validateForm('#dep-new-container')) {
            const hospital = Template.dashboard.currentHospital.get();
            const deps = hospital.departments;
            deps.push({
                name: $('#dep-name-new').val(),
                maxSimultaneousSessions: $('#dep-max-sessions-new').val(),
            });
            Meteor.call('updateHospitalDepartments', hospital.id, deps, (err, data) => {
                if (!err) {
                    Template.dashboard.currentHospital.set(data);
                    sAlert.success('Hospital department added.');
                    $(e.target).closest('.card').find('input:not(.disabled),select:not(.disabled)')
                        .prop('disabled', true);
                    $(e.target).closest('.card').find('.toggle')
                        .toggle();
                    $('#dep-name-new').val('');
                    $('#dep-max-sessions-new').val('');
                } else {
                    sAlert.error('Adding department failed.');
                }
            });
        }
    },

    // delete a department
    'click .department .fa-trash': (e) => {
        const depId = $(e.target).closest('.fa-trash').attr('data-department-id');
        const hospital = Template.dashboard.currentHospital.get();
        // find the department to delete
        let departmentToDelete = {};
        hospital.departments.forEach((dep) => {
            if (dep.id === depId) {
                departmentToDelete = dep;
            }
        });

        console.log(departmentToDelete);
        // perform the api request
        Meteor.call('deleteHospitalDepartment', hospital.id, departmentToDelete, (err, data) => {
            if (!err) {
                // all good
                Template.dashboard.currentHospital.set(data);
                sAlert.success('Hospital department deleted.');
            } else {
                // deletion failed
                sAlert.error('Failed to delete department');
            }

        });
    },

    // save all departments
    'click #save-departments': () => {
        const hospital = Template.dashboard.currentHospital.get();
        const deps = [];
        $('.department').each((index, item) => {
            deps.push({
                name: $(item).find('input[name="dep-name"]').val(),
                maxSimultaneousSessions: $(item).find('input[name="dep-sessions"]').val(),
                id: $(item).attr('data-department-id'),
            });
        });

        Meteor.call('updateHospitalDepartments', hospital.id, deps, (err, data) => {
            if (!err) {
                Template.dashboard.currentHospital.set(data);
                sAlert.success('Hospital departments updated.');
            } else {
                // update failed
                sAlert.error('Failed to update departments');
            }
        });
    },


    // add a new specialist
    'click #add-specialist-button': (e) => {
        const depId = $(e.target).closest('[data-department-id]').attr('data-department-id');
        if (validateForm(`#dep-new-specialist-${depId}`)) {
            const newSpecialist = {
                label: $(`#dep-new-specialist-${depId}`).val(),
            };
            const hospital = Template.dashboard.currentHospital.get();
            hospital.departments.forEach((dep) => {
                if (dep.id === depId) {
                    newSpecialist.departmentName = dep.name;
                    newSpecialist.department = dep.name;
                }
            });
            Meteor.call('addSpecialistType', hospital.id, newSpecialist, (err, data) => {
                if (!err) {
                    sAlert.success('Specialist type successfully created.');
                    Template.dashboard.currentHospital.set(data);
                    $(`#dep-new-specialist-${depId}`).val('');
                    setTimeout(() => {
                        $('.toggle.delete-spec:hidden').show();
                    }, 100);
                } else {
                    sAlert.error('Failed to create specialist type.');
                }
            });
        }
    },


    // delete a specialist
    'click .delete-spec': (e) => {
        const specId = $(e.target).closest('[data-specialist-id]').attr('data-specialist-id');
        const hospital = Template.dashboard.currentHospital.get();
        let specToDelete = {};

        hospital.specialistTypes.forEach((spec) => {
            if (spec.id === specId) {
                specToDelete = spec;
            }
        });

        Meteor.call('deleteSpecialistType', hospital.id, specToDelete, (err, data) => {
            if (!err) {
                Template.dashboard.currentHospital.set(data);
                sAlert.success('Hospital specialist deleted.');
            } else {
                // update failed
                sAlert.error('Failed to delete specialist');
            }
        });
    },

});
