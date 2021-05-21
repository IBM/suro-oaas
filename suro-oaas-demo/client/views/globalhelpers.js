/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 * Global template helpers and utilities.
 *
 */
import moment from 'moment'

/*
 *  BOILERPLATE CORE HELPERS
 */
/**
 * Default helper that returns the provided value if undefined and the provided default fallback if not
 */
Template.registerHelper('default', (value, defaultFallback) => {
    if (value === undefined || value == null) {
        return defaultFallback;
    }
    return value;
});


/**
 * Logical AND for up to 5 operands.
 */
Template.registerHelper('and', (a, b, c, d, e) => {
    if (a !== undefined) {
        if (b !== undefined) {
            let res = (a && b);
            if (c !== undefined) {
                res = (c && res);
                if (d !== undefined) {
                    res = (d && res);
                    if (e !== undefined) {
                        res = (e && res);
                    }
                }
            }
            return res;
        }

        return (a !== false);

    }

    return false;
});

/**
 * Logical OR for up to 5 operands.
 */
Template.registerHelper('or', (a, b, c, d, e) => {
    if (a !== undefined) {
        if (b !== undefined) {
            let res = (a || b);
            if (c !== undefined) {
                res = (c || res);
                if (d !== undefined) {
                    res = (d || res);
                    if (e !== undefined) {
                        res = (e || res);
                    }
                }
            }
            return res;
        }

        return (a !== false);

    }

    return false;
});
/**
 * Returns the size of an array or a collection.
 */
Template.registerHelper('sizeOf', (item) => {
    if (item === undefined || item == null) {
        return 0;
    } else if ('length' in item) {
        return item.length;
    } else if ('collection' in item) {
        return item.count();
    }
    return null;
});

Template.registerHelper('isDefined', (item) => {
    if (item === undefined || item === null) {
        return false;
    }
    return true;

});

/**
 * Checks a string, array or collection if it doesn't have any elements, is empty or undefined.
 */
Template.registerHelper('isEmpty', (item) => {
    if (item === undefined || item == null) {
        return true;
    } else if ('length' in item) {
        // empty array
        return (item.length === 0);
    } else if ('collection' in item) {
        // empty collection
        return (item.count() === 0);
    }
    // empty string / empty object
    return (item === '' || item === {});
});

/**
 * Checks a string, array or collection if it doesn't have any elements, is empty or undefined.
 */
Template.registerHelper('isNotEmpty', (item) => {
    if (item === undefined || item == null) {
        return false;
    } else if ('length' in item) {
        // empty array
        return (item.length !== 0);
    } else if ('collection' in item) {
        // empty collection
        return (item.count() !== 0);
    }
    // empty object
    return (item !== '' && item !== {});
});

/**
 * Array search helper. Returns true or false, if the array contains the search value.
 */
Template.registerHelper('contains', (haystack, needle) => haystack.indexOf(needle) !== -1);


/**
 * Uses a predefined format to display a date object. The invalidDefault is the fallback to display in case the date is
 * not valid (e.g. null, undefined, ...). It can be set to something like 'tba' or 'tbd'. If no default is provided n/a
 * is returned.
 */
Template.registerHelper('formatDateDefault', (date, invalidDefault) => {
    const mom = moment(date);
    if (mom.isValid() && date !== undefined) {
        return mom.format('ddd, D MMM YYYY, HH:mm');
    } else if (invalidDefault !== undefined && invalidDefault.hash === undefined) {
        return invalidDefault
    }

    return 'n/a';
});

/**
 * Formats the given date with the provided date. A format is specified as described here:
 * http://momentjs.com/docs/#/displaying/format/
 *
 * The invalidDefault is displayed if the date is invalid. It should be something like 'tbd' or 'tba'.
 */
Template.registerHelper('formatDate', (date, format, invalidDefault) => {
    const mom = moment(date);
    if (mom.isValid() && date !== undefined) {
        return mom.format(format);
    } else if (invalidDefault !== undefined && invalidDefault.hash === undefined) {
        return invalidDefault
    }

    return 'n/a';
});

/**
 * This function should be used if you have a plain textarea, but want to display linebreaks as they were provided in
 * the textarea. If you don't use the nl2br function, linebreaks (\n) provided in the textarea will not be recognized.
 *
 * To make sure it is rendered properly, use the 3-bracket notation: {{{nl2br myText}}} to ensure the <br/> tags are
 * rendered and not just printed out as text.
 */
Template.registerHelper('nl2br', (str) => {
    if (str === undefined || str == null) {
        return '';
    }
    const breakTag = '<br/>';
    return (`${str}`).replace(/([^>\r\n]?)(\r\n|\n\r|\r|\n)/g, `$1${breakTag}$2`);
});

/**
 * Concatenates a list of entries (should be primitive objects) and joins them using a the provided symbol. If no symbol
 * is provided the entries will be joined using a comma (and space).
 */
Template.registerHelper('concat', (list, symbol) => {
    if (symbol === undefined || symbol == null) {
        return list.join(', ');
    }
    return list.join(symbol)
});

/**
 * Generic comparison, simply compares 2 values and returns their equality.
 */
Template.registerHelper('equals', (a, b) => (a === b));


Template.registerHelper('notEquals', (a, b) => (a !== b));

/**
 * Helper to return if the currently logged in user is an admin.
 */
Template.registerHelper('isAdmin', () => {
    if (Meteor.user() && Meteor.user().profile !== undefined) {
        return (Meteor.user().profile.accessLevel === 2)
    }
    return false;
});


Template.registerHelper('directEqualsHide', (equals) => {
    if (equals === true) {
        return 'display: none;';
    }

    return '';
});

Template.registerHelper('directNotEqualsHide', (equals) => {
    if (equals === false) {
        return 'display: none;';
    }

    return '';
});

Template.registerHelper('equalsHide', (a, b) => {
    if (a === b) {
        return 'display: none;';
    }

    return '';
});

Template.registerHelper('notEqualsHide', (a, b) => {
    if (a !== b) {
        return 'display: none;';
    }

    return '';
});

Template.registerHelper('greaterThan', (a, b) => (a > b));


Template.registerHelper('roundNumber', (number, decimals) => {
    if (decimals === undefined) {
        return Math.round(number);
    }
    const factor = decimals * 10;
    return (Math.round(number * factor)) / factor;

});

Template.registerHelper('for', (indexFrom, indexTo) => {
    const result = [];
    if (indexFrom > indexTo) {
        for (let i = indexFrom; i >= indexTo; i--) {
            result.push({ value: i });
        }
    } else {
        for (let i = indexFrom; i <= indexTo; i++) {
            result.push({ value: i });
        }
    }
    return result;
});

/**
 * Similar to the equals helper, this helper will return ' active' if 2 values are equal. This is very useful for HTML
 * elements to add the class 'active' to an element fulfilling the criteria.
 */
Template.registerHelper('isActiveClass', (a, b) => {
    if (a === b) {
        return ' active';
    }
    return '';
});

/**
 * Returns the given class, if boolean is true.
 */
Template.registerHelper('addClass', (boolean, className) => {
    if (boolean) {
        return className;
    }

    return '';
});


Handlebars.registerHelper('lostConnection', () => Session.get('lostConnection'));

Template.registerHelper('currentRun', () => currentRun.get());

Template.registerHelper('currentUser', () => {
    if (Meteor.userId()) {
        return true;
    }

    return false;
});
