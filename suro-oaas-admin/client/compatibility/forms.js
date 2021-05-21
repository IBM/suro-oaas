/**
 * Performs a form validation on a given DOM element. Any input, select or textarea tag can have an attribute
 * data-validate="true" which puts the form element on the list of elements to check. The function will determine the
 * current value / selected option of the form element and if the value is not provided or an empty string, the
 * validation will fail. Besides returning a validation result, it will also add the .error class to any element that
 * may have caused the v
 *
 * @param {String} selector - the CSS selector for the form / container holding the form elements
 * @returns {boolean} - the validation result
 */
validateForm = function(selector) {
    var validationResult = true;
    $(selector).find('input,select,textarea').removeClass('error');
    $(selector).find('input,select,textarea').each(function(index, item) {
        if ($(item).attr('data-validate') === 'true' && $(item).is(':visible')) {
            var value = $(item).val();
            if (item.tagName === 'SELECT') {
                value = $(item).find('option:selected').val();
            }

            if ($(item).attr('data-prevent-trim') !== 'true') {
                value = value.trim();
            }
            if (value === '' || value === undefined || value == null) {
                $(item).addClass('error');
                validationResult = false;
            }
        }
    });

    return validationResult;
}
