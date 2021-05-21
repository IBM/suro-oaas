_findMaxPeriod = function(items) {
    const testArray = [];
    items.forEach((item) => {
        testArray.push(item.period);

    });
    return Math.max.apply(null, testArray);
};

_findMinPeriod = function(items) {
    const testArray = [];
    items.forEach((item) => {
        testArray.push(item.period);

    });
    return Math.min.apply(null, testArray);
};

// helper functions used for the computation (see below) on the summary tab

_getAllPatients = function(waitingList, period) {
    let sum = 0;

    waitingList.forEach((item) => {
        if (item.period === period) {
            sum += item.value;
        }

    });
    return sum

}

_getAllPatientsForCategory = function(waitingList, period, cat2) {
    let sum = 0;

    waitingList.forEach((item) => {
        if (item.period === period && item.cat === cat2) {
            sum += item.value;
        }
    });
    return sum

}

// the following functions compute values directly used by the summary tab

_patientsOnWatingFirstDay = function(myRun) {
    const minPeriod = myRun.waitListTotal;
    return _getAllPatients(minPeriod, _findMinPeriod(minPeriod));
}

_patientsOnWatingListLastDay = function(myRun) {
    const maxPeriod = myRun.waitListTotal;
    return _getAllPatients(maxPeriod, _findMaxPeriod(maxPeriod));

};

_patientsOverdueLastDay = function(myRun) {
    const maxPeriod = myRun.waitListOverdue;
    return _getAllPatients(maxPeriod, _findMaxPeriod(maxPeriod));
};

_patientsOverdueFirstDay = function(myRun) {
    const minPeriod = myRun.waitListOverdue;
    return _getAllPatients(minPeriod, _findMinPeriod(minPeriod));
}


_patientsOnWatingListLastDayByCat = function(myRun, cat) {
    const maxPeriod = myRun.waitListTotal;
    return _getAllPatientsForCategory(maxPeriod, _findMaxPeriod(maxPeriod), cat);

};


_totalCalculator = function(items) {
    let total = 0;
    items.forEach((item) => {
        total += item.value;

    });
    return total;
};


_totalOccupancy = function(myRun) {
    let total = 0;
    myRun.bedTotalRes.forEach((item) => {
        total += item.value;
    });
    return total;

};


_totalAvailability = function(myRun) {
    let total = 0;
    myRun.bedAvailRes.forEach((item) => {
        total += item.value;
    });
    return total;
};


_totalWaitList = function(myRun) {
    let total = 0;
    myRun.waitListTotal.forEach((item) => {
        total += item.value;
    });
    return total;
};

_totalWaitListOverdue = function(myRun) {
    let total = 0;
    myRun.waitListOverdue.forEach((item) => {
        total += item.value;
    });
    return total;
};
