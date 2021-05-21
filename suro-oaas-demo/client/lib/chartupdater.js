/**
 * Created by Peter Ilfrich
 * Copyright (c) 2015 IBM Research. All rights reserved.
 *
 *
 */

ChartUpdater = function(chart) {
    this.chart = chart;
}


const weekdays = {
    0: 'Mon',
    1: 'Tue',
    2: 'Wed',
    3: 'Thu',
    4: 'Fri',
    5: 'Sat',
    6: 'Sun',
}

ChartUpdater.prototype.handleChartUpdate = function(result, successCallback) {

    // handle x-axis ticks
    try {
        const dataLength = result.data.columns[0].length - 1;

        let categories = [];
        if (dataLength === 28) {
            for (let i = 0; i < 28; i++) {
                categories.push(weekdays[i % 7]);
            }
        } else if (dataLength === 20) {
            for (let i = 0; i < 20; i++) {
                categories.push(weekdays[i % 5]);
            }
        } else if (dataLength === 4) {
            categories = ['Week 1', 'Week 2', 'Week 3', 'Week 4'];
        } else if (dataLength === 5) {
            categories = ['Start', 'Week 1', 'Week 2', 'Week 3', 'Week 4']
        }

        if (categories.length !== 0) {

            if (result.axis === undefined) {
                result.axis = {};
            }

            result.axis.x = {
                type: 'category',
                categories,
            }
            if (categories.length === 5) {
                result.axis.x.tick = {
                    centered: true,
                }
            }
        }


    } catch (err) {
        console.log('Could not determine data length');
        console.log(err);
    }

    if (this.chart === undefined) {

        const self = this;
        setTimeout(() => {
            self.chart = c3.generate(result);
            if (successCallback !== undefined) {
                successCallback();
            }

        }, 500);

    } else {

        // remove data
        const unloads = [];
        this.chart.data().forEach((dataSeries) => {

            let stay = false;
            result.data.columns.forEach((col) => {
                if (dataSeries.id === col[0]) {
                    stay = true;
                }
            });

            if (!stay) {
                unloads.push(dataSeries.id);
            }

        });


        // append the unloads
        result.data.unload = unloads;
        this.chart.load(result.data);


        if (successCallback !== undefined) {
            successCallback();
        }

    }
}


ChartUpdater.prototype.resetChart = function() {
    if (this.chart !== undefined) {
        this.chart.destroy();
        this.chart = undefined;
    }
}
