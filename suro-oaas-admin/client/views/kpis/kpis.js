/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */
Template.kpis.helpers({

    enrichedCategories: (categories) => {

        const result = [];

        categories.forEach((cat) => {

            const entry = {
                label: cat.label,
                daysToAdmission: cat.maxWaitListStay,
                maxPoints: cat.possiblePoints,
                periods: [],
                id: cat.id,
                minPointsRequired: cat.minPointsRequired,
            };

            for (let period = 1; period <= 2; period++) {
                const currentPeriod = {
                    period,
                    possiblePoints: cat.possiblePoints,
                    pointRanges: [],
                }

                // fetch target percentage from KPI targets
                cat.kpiTargets.forEach((kpi) => {
                    // pick the right period
                    if (kpi.interval === period * 2 && kpi.numberOfPoints === currentPeriod.possiblePoints) {
                        // set target for current period
                        currentPeriod.targetPercentage = kpi.requiredOnTimePerformance * 100;
                    }
                });

                // set belowTarget and points
                cat.kpiTargets.forEach((kpi) => {
                    // pick the right period
                    if (kpi.interval === period * 2) {

                        // ignore the target percentage KPI target (handled above)
                        if (kpi.numberOfPoints !== currentPeriod.possiblePoints) {

                            // populate ranges
                            currentPeriod.pointRanges.push({
                                belowTarget: currentPeriod.targetPercentage - (kpi.requiredOnTimePerformance * 100),
                                points: kpi.numberOfPoints,
                            });

                        }
                    }
                });

                // sort ranges in descending order
                currentPeriod.pointRanges.sort((a, b) => a.points > b.points);

                // set fromBelowTarget
                const newRanges = [];
                currentPeriod.pointRanges.forEach((range) => {
                    if (currentPeriod.possiblePoints !== range.points + 1) {
                        // find previous range and add +1 percentage
                        currentPeriod.pointRanges.forEach((otherRange) => {
                            if (range.points + 1 === otherRange.points) {
                                range.fromBelowTarget = otherRange.belowTarget + 1;
                            }
                        });
                    }
                    newRanges.push(range);
                });

                // write back to current period and add period to entry result
                newRanges.reverse();
                currentPeriod.pointRanges = newRanges;
                entry.periods.push(currentPeriod);
            }

            // add ui relevant parameters
            entry.uiTotalWidth = (entry.maxPoints === 1) ? 12 : 6;


            result.push(entry);

        });

        return result;

        // example
        /*
        const entry = {
            label: 'Cat 2',
            daysToAdmission: 90,
            maxPoints: 3,
            minPointsRequired: 0,
            periods: [
                {
                    targetPercentage: 75,
                    possiblePoints: 3,
                    period: 1,
                    pointRanges: [
                        {
                            belowTarget: 5,
                            points: 2,
                        },
                        {
                            belowTarget: 10,
                            points: 1,
                            fromBelowTarget: 6,
                        },
                    ],
                },
                {
                    targetPercentage: 80,
                    possiblePoints: 3,
                    period: 2,
                    pointRanges: [
                        {
                            belowTarget: 5,
                            points: 2,
                        },
                        {
                            belowTarget: 10,
                            points: 1,
                            fromBelowTarget: 6,
                        },
                    ],
                },
            ],

        }
        return [];
        */
    },

});

Template.kpis.events({
    'click #save-kpis': (e) => {

        // validate form


        // TODO: add support for region update

        const categories = [];
        // for each category (extract id from the item)
        $('input[name="maxwait-cat"]').each((index, item) => {
            const category = {
                label: $(item).attr('data-label'),
                id: $(item).attr('data-id'),
                maxWaitListStay: $(item).val(),
                kpiTargets: [],
            };

            category.minPointsRequired = $(`.category-container[data-category-id="${category.id}"]`).attr('data-min-points-required');
            let maxPoints = 0;

            // for each period
            $(`.category-container[data-category-id="${category.id}"] .category-period`).each((pIndex, pItem) => {
                const period = parseInt($(pItem).attr('data-period'), 10);
                const target = parseFloat($(`#cat-target-${category.id}-${period}`).val()) / 100.0;
                const points = parseInt($(`#cat-points-${category.id}-${period}`).val(), 10);

                // push target
                category.kpiTargets.push({
                    interval: period * 2,
                    numberOfPoints: points,
                    requiredOnTimePerformance: target,
                });

                // update max
                maxPoints = Math.max(maxPoints, points)

                // for each range
                $(pItem).find('.point-range').each((rangeIndex, rangeItem) => {
                    category.kpiTargets.push({
                        interval: period * 2,
                        numberOfPoints: parseInt($(rangeItem).find('input[name="kpi-points"]').attr('data-points'), 10),
                        requiredOnTimePerformance: target - (parseFloat($(rangeItem).find('input[name="kpi-below-target"]').val()) / 100.0),
                    });
                });
            });

            // duplicate intervals
            category.kpiTargets.forEach((kpi) => {
                category.kpiTargets.push({
                    interval: kpi.interval - 1,
                    numberOfPoints: kpi.numberOfPoints,
                    requiredOnTimePerformance: kpi.requiredOnTimePerformance,
                });
            });
            category.possiblePoints = maxPoints;

            // push to result
            categories.push(category);

        });

        // save urgency categories
        Meteor.call('updateUrgencyCategories', Template.dashboard.currentHospital.get().id, categories, (err, data) => {
            if (!err) {
                sAlert.success('Updated urgency categories');
                Template.dashboard.currentHospital.set(data);

                $(e.target).closest('.card').find('input,select')
                    .prop('disabled', true);
                $(e.target).closest('.card').find('.toggle')
                    .toggle();
                $(e.target).closest('.card').find('.section-title')
                    .removeClass('edit');
            } else {
                sAlert.error('Error updating urgency categories.');
            }
        });

    },

});
