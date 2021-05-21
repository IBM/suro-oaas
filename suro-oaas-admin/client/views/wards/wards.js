/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */
Template.wards.helpers({});

Template.wards.events({

    // add a new ward row
    'click #new-ward-button': () => {
        if ($('.new-ward-row:visible').length > 0) {
            // copy row and empty values
            $('.new-ward-row').clone().appendTo('#new-ward-container');
            $('.new-ward-row:last-child input').val('');
        } else {
            // show row
            $('#new-ward-container').show();
        }
    },

    // delete new ward row
    'click #new-ward-container .fa-trash': (e) => {
        if ($('.new-ward-row').length === 1) {
            $(e.target).closest('#new-ward-container').hide();
        } else {
            $(e.target).closest('.new-ward-row').remove();
        }
    },


    // save list of wards
    'click #save-wards': (e) => {
        const wardList = [];
        const hospital = Template.dashboard.currentHospital.get();

        // fetch current ward data
        $('.ward').each((index, item) => {
            const wardId = $(item).attr('data-ward-id');
            wardList.push({
                id: wardId,
                name: $(`#ward-name-${wardId}`).val(),
                bedsCount: $(`#ward-beds-${wardId}`).val(),
            });
        });

        // check new wards
        $('#new-ward-container .new-ward-row:visible').each((index, item) => {
            const name = $(item).find('input[name="ward-name-new"]').val();
            const beds = $(item).find('input[name="ward-beds-new"]').val();
            if (name.trim() !== '') {
                wardList.push({
                    name,
                    bedsCount: beds === '' ? 0 : beds,
                });
            }
        });

        // update data
        Meteor.call('updateHospitalWards', hospital.id, wardList, (err, data) => {
            if (!err) {
                sAlert.success('Wards successfully updated.');

                $(e.target).closest('.card').find('input,select')
                    .prop('disabled', true);
                $(e.target).closest('.card').find('.toggle')
                    .toggle();
                $(e.target).closest('.card').find('.section-title')
                    .removeClass('edit');
                $('#new-ward-container').hide();
                $('input[name="ward-name-new"]').val('');
                $('input[name="ward-beds-new"]').val('');
                Template.dashboard.currentHospital.set(data);
            } else {
                sAlert.error('Wards update failed.');
            }
        });
    },


    // delete a ward
    'click .ward .fa-trash': (e) => {
        const wardId = $(e.target).closest('[data-ward-id]').attr('data-ward-id');
        const hospital = Template.dashboard.currentHospital.get();
        let wardToDelete = {};

        hospital.wards.forEach((ward) => {
            if (ward.id === wardId) {
                wardToDelete = ward;
            }
        });

        Meteor.call('deleteHospitalWard', hospital.id, wardToDelete, (err, data) => {
            if (!err) {
                sAlert.success('Ward successfully deleted.');
                Template.dashboard.currentHospital.set(data);
            } else {
                sAlert.error('Failed to delete ward.');
            }
        });

    },

});
