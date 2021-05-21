/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */
Meteor.methods({

    getHospital: () => httpCall('get', `${SERVICE_URL}/api/hospitals`).data[0],

    updateHospitalMeta: (id, data) => httpCall('put', `${SERVICE_URL}/api/hospitals/${id}/meta`, data).data,


    updateHospitalDepartments: (id, data) => httpCall('put', `${SERVICE_URL}/api/hospitals/${id}/departments`, data).data,

    deleteHospitalDepartment: (id, department) => httpCall('del', `${SERVICE_URL}/api/hospitals/${id}/department`, department).data,


    addSpecialistType: (id, specialist) => httpCall('post', `${SERVICE_URL}/api/hospitals/${id}/specialistType`, specialist).data,

    deleteSpecialistType: (id, specialist) => httpCall('del', `${SERVICE_URL}/api/hospitals/${id}/specialistType`, specialist).data,


    updateHospitalWards: (id, wards) => httpCall('put', `${SERVICE_URL}/api/hospitals/${id}/wards`, wards).data,

    deleteHospitalWard: (id, ward) => httpCall('del', `${SERVICE_URL}/api/hospitals/${id}/ward`, ward).data,

    updateUrgencyCategories: (id, categories) => httpCall('put', `${SERVICE_URL}/api/hospitals/${id}/urgencyCategory`, categories).data,

});
