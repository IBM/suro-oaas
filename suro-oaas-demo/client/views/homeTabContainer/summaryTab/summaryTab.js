const carDirection = function(val) {
    const caret = {
        direction: 'up',
        color: '#FF0000',
    }
    if (val < 0) {
        caret.direction = 'down';
        caret.color = '#10A45B'
    }
    return caret;

}


Template.summaryTab.weekify = function(col) {
    const month = [];

    month.push(col[0])
    month.push(col[1])

    for (let i = 7; i < col.length; i += 7) {
        month.push(col[i])
    }

    return month;
}


Template.summaryTab.helpers({


    myStrategy () {
        return currentRun.get();
    },


    summarySolution() {
        if (currentSolution.get() == null || currentSolution.get() === '') {
            return true
        }

        return false
    },


    bedUtilisation () {
        const solution = currentSolution.get();
        if (solution == null) {
            return 0;
        }
        const totalOccupancy = _totalCalculator(solution.bedTotalRes);
        const totalAvailability = _totalCalculator(solution.bedAvailRes);
        const totalUtilisation = (totalOccupancy / totalAvailability) * 100;
        return parseInt(totalUtilisation, 10);
    },
    icuUtilisation () {
        const solution = currentSolution.get();
        if (solution == null) {
            return 0;
        }
        const totalOccupancy = _totalCalculator(solution.icuTotalRes);
        let totalAvailability = _totalCalculator(solution.icuAvailRes);

        const weeks = totalAvailability / 7;
        totalAvailability -= (weeks * 2);

        const totalUtilisation = (totalOccupancy / totalAvailability) * 100;
        return parseInt(totalUtilisation, 10);

    },
    waitingList () {
        const solution = currentSolution.get();
        // console.log('solution',solution)
        if (solution == null) {
            return 0;
        }

        const patientsOnWatingListLastDay = _patientsOnWatingListLastDay(solution);
        const patientsOnWatingListFirstDay = _patientsOnWatingFirstDay(solution);

        const patientsChange = ((patientsOnWatingListLastDay - patientsOnWatingListFirstDay) / patientsOnWatingListFirstDay) * 100;

        const patientsCat1 = _patientsOnWatingListLastDayByCat(solution, 1);
        const patientsCat2 = _patientsOnWatingListLastDayByCat(solution, 2);
        const patientsCat3 = _patientsOnWatingListLastDayByCat(solution, 3);

        const list = {
            change: parseFloat(patientsChange).toFixed(2),
            cat1: patientsCat1,
            cat2: patientsCat2,
            cat3: patientsCat3,
        }
        list.caret = carDirection(list.change);
        return list;

    },
    overduePatients () {
        const solution = currentSolution.get();
        if (solution === undefined) {
            return 0;
        }

        const patientsOverdueLastDay = _patientsOverdueLastDay(solution);
        const patientsOverdueFirstDay = _patientsOverdueFirstDay(solution);

        const patientsChange = ((patientsOverdueLastDay - patientsOverdueFirstDay) / patientsOverdueFirstDay) * 100;
        const list = {
            change: parseFloat(patientsChange).toFixed(2),
            totalLastDay: patientsOverdueLastDay,
        }
        list.caret = carDirection(list.change);
        return list;

    },


});

Template.summaryTab.events({
    'click .toggleMore': () => {
        const e = document.getElementById('toggleDiv');
        if (e.style.display === 'block') {
            // e.style.display = 'none';
            $('.toggleDiv').css('display', 'none');
            $('.chevron').removeClass('fa-chevron-up');
            $('.chevron').addClass('fa-chevron-down');


            $('.toggleText').text('More')
        } else {
            $('.toggleDiv').css('display', 'block');
            $('.chevron').removeClass('fa-chevron-down');
            $('.chevron').addClass('fa-chevron-up');

            $('.toggleText').text('Less');

        }

    },
});

Template.summaryTab.rendered = function() {

};
