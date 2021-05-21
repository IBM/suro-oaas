const colors = ['#F33B52', '#EF7928', '#C8C02E', '#10A45B'];


getPeriod = function() {
    let date = currentRun.get().startTime;
    date = new Date(date);
    return Math.ceil((date.getMonth() + 1) / 3);
}

const getColor = (val) => {

    if (val <= 0.25) {
        return colors[0]
    }
    if (val <= 0.50) {
        return colors[1]
    }
    if (val <= 0.75) {
        return colors[2]
    }
    if (val <= 1) {
        return colors[3]
    }
    return colors[0];
}


_getKPI = function(cat, period, value, data) {
    let kpi = currentHospital.get().urgencyCategories[cat - 1];

    const targets = [];
    kpi = kpi.kpiTargets;

    kpi.forEach((item) => {
        if (item.interval === period) {
            targets.push(item.requiredOnTimePerformance);
        }
    })
    targets.sort();
    if (value <= targets[0]) {
        const values = {
            points: 1,
            threshold: targets[0],
        }
        return values;
    }
    if (value <= targets[1]) {
        const values = {
            points: 2,
            threshold: targets[1],
        }
        return values;
    }
    if (value <= targets[2] || value >= targets[2]) {
        const values = {
            points: 3,
            threshold: targets[2],
        }
        return values;
    }


    return {};
}

_findItemInTargetRes = function(results, cat, period) {

    let returnResult = null;
    results.forEach((item) => {
        if (item.timePeriod === period && item.targetType === cat) {
            returnResult = item;
        }
    });
    return returnResult;
};

Template.slider.helpers({


    slideValues() {

        const runPeriod = getPeriod();
        const results = currentSolution.get().targetsRes;
        const t = _findItemInTargetRes(results, this.cat, this.per);

        const d = {
            fill: t.currentValue * 100,
            indicator: 100,
            points: 1,
            fillColor: getColor(t.currentValue),
        };

        if (this.cat !== 1) {
            const points = _getKPI(this.cat, this.per, t.currentValue);
            d.indicator = points.threshold * 100;
            d.points = points.points;

        }

        if (runPeriod !== this.per) {
            d.fillColor = '#c0c0c0'
        }

        if (runPeriod < this.per) {
            d.fill = 0;
        }

        return d;


    },

    fillColor() {
        return colors[this.data.points];


    },

});

Template.slider.events({});

Template.slider.rendered = function() {

};
