/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 * Form handling functions
 */

/**
 * Validates any given customer form, handling highlighting (red-border) and button changes (blocking)
 *
 * @param {String} jqSelector - a JQuery selector specifying the root of the form DOM or an array of selectors
 * @returns {boolean} true/false depending on the outcome of the validation
 */
function validateForm(jqSelector) {
    // init validation result
    var result = true;
    var formSelector = jqSelector;

    // if validation is blocked
    if (formSelector instanceof Array) {
        var abort = false;
        formSelector.forEach(function(item) {
            if ($(item).attr('data-block-validation') === 'true') {
                abort = true;
            }
        });
        if (abort) {
            return true;
        }
    } else if ($(formSelector).attr('data-block-validation') === 'true') {
        return true;
    } else {
        // if form selector is not an array of selectors, convert it
        formSelector = [formSelector];
    }

    // remove all pre-existing error markers
    $('[data-validate="true"]').removeClass('error');

    // for each selector in the validator array, validate the visible form elements
    formSelector.forEach(function(form) {
        // validate form
        $(`${form} [data-validate="true"]:visible`).each(function(index, item) {
            var value = $(item).val();
            if ($(item).tagName === 'SELECT') {
                value = $(item).find('option:selected').val();
            }

            // validate field and adjust classes
            if (value == null || value === '') {
                // let overall form validation fail
                result = false;
                $(item).addClass('error');
            } else {
                $(item).removeClass('error');
            }
        });
    });

    // return validation result
    return result;
}
