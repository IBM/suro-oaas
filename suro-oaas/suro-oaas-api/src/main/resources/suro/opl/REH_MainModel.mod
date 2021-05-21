/*********************************************
 * OPL 12.5 Model
 * Author: Administrator
 * Creation Date: Mar 18, 2014 at 3:52:54 AM
 *********************************************/

// Input

tuple TAllocatedSessionsFixed {
  key int departmentId;
  key int period;
  int value;
};

tuple TArrivingPatientInfo {
  key int surgeryID;
  key int period;
  int nPatients;
};

tuple TBaseConfigParams {
  int nPeriods;
  int sessionLength;
};

tuple TDepartments {
  key int id;
  string name;
  int maxSessionsPerPeriod;
};

tuple TFixedParams {
  int nPoints;
  int nPerformanceMeasurements;
  int basePlanCycleLength;
  int nCategories;
};

tuple TInitialPatientInfo {
  key int surgeryID;
  key int periodsLeft;
  int nPatients;
};

tuple TObjFunParams {
  int minPoints;
  int bedsPerWorkload;
  int durationPerWorkload;
};

tuple TOptimisationParams {
  float gap;
  int minIntegerPeriod;
  int integerWindowSize;
  int timeLimit;
};

tuple TRollingTimeParams {
  int rollingTimeStepSize;
  int startPeriod;
};

tuple TScenarioParams {
  int maxTotalSessions;
  int allowEmptySessions;
  int maxChanges;
  int maxAdditions;
  int maxDeletions;
  string runName;
  int nTotalOtsessions;
  int nIcupatientsPerDay;
  int allowCat1Violation;
};

tuple TSpecialistIds {
  key int id;
};

tuple TSpecialistInfo {
  key int id;
  key int period;
  int availability;
};

tuple TTimingParameters {
  int nTotalPeriods;
  int nWorkingDays;
};

tuple TTreatedPatientsFixed {
  key int surgeryId;
  key int period;
  int value;
};

tuple TWardIds {
  key int id;
};

tuple TWardInfo {
  key int id;
  key int period;
  int nBeds;
};
{TDepartments} DEPARTMENTS = ...;
{TSpecialistInfo} SPECIALIST_INFO = ...;
{TWardInfo} WARD_INFO = ...;
{TAllocatedSessionsFixed} allocatedSessionsFixed = ...;
{TArrivingPatientInfo} arrivingPatientInfo = ...;
TBaseConfigParams baseConfigParams = ...;
TFixedParams fixedParams = ...;
{TInitialPatientInfo} initialPatientInfo = ...;
TObjFunParams objFunParams = ...;
TOptimisationParams optimisationParams = ...;
TRollingTimeParams rollingTimeParams = ...;
TScenarioParams scenarioParams = ...;
{TSpecialistIds} specialistIds = ...;
TTimingParameters timingParams = ...;
{TTreatedPatientsFixed} treatedPatientsFixed = ...;
{TWardIds} wardIds = ...;

tuple TBasePlanInfo {
  key TDepartments departmentID;
  key int period;
  int nSessions;
};

tuple TSurgeries {
  key int id;
  string name;
  TDepartments departmentID;
  float duration;
  float changeoverTime;
  float los;
  float pICU;
  int wardID;
  int specialistID;
  int cat;
  float wies;
};
{TBasePlanInfo} BASE_PLAN_INFO with departmentID in DEPARTMENTS = ...;
range TSurgeries_cat_range = 1..fixedParams.nCategories;
{TSurgeries} SURGERIES with departmentID in DEPARTMENTS, cat in TSurgeries_cat_range = ...;
int endDayOfPerformanceMeasurement [1..fixedParams.nPerformanceMeasurements] = ...;
float fractionForPoint [2..fixedParams.nCategories][1..fixedParams.nPerformanceMeasurements][1..fixedParams.nPoints] = ...;
int maxWaitListStay [1..fixedParams.nCategories] = ...;
int nOnTimeBeforeHorizon [2..fixedParams.nCategories] = ...;
int nOverDueBeforeHorizon [2..fixedParams.nCategories] = ...;

// Output

tuple TBedStayCategories {
  key string name;
};

tuple TBeds {
  key TDepartments departmentId;
  key int period;
  float value;
};

tuple TDepartmentNames {
  key string name;
  int id;
};

tuple TIcu {
  key TDepartments departmentId;
  key int period;
  float value;
};

tuple TNAddedSessions {
  key TDepartments _0DepartmentsId;
  key int _1CyclesValue;
  float value;
};

tuple TNAllocatedSessions {
  key TDepartments _0DepartmentsId;
  key int _1CyclesValue;
  int value;
  string departmentName;
};

tuple TNDeletedSessions {
  key TDepartments _0DepartmentsId;
  key int _1CyclesValue;
  float value;
};

tuple TNPatientsTreatedResult {
  key int period;
  key TSurgeries surgeriesId;
  key int periodsLeft;
  int value;
};

tuple TNTotalChanges {
  key TDepartments departmentId;
  key int _1CyclesValue;
  float value;
};

tuple TNTreatedPatients {
  key TSurgeries _0SurgeriesId;
  key int _1PeriodsValue;
  float value;
};

tuple TNTreatedPatientsOnTimePerPeriod {
  key TSurgeries _0SurgeriesId;
  key int _1PeriodsValue;
  float value;
};

tuple TNTreatedPatientsOverDuePerPeriod {
  key TSurgeries _0SurgeriesId;
  key int _1PeriodsValue;
  float value;
};

tuple TPatientOutputData {
  key int period;
  int waiting;
  int incoming;
  int treated;
  int stillWaiting;
  int overDueWaiting;
  int overDueIncoming;
  int overDueTreated;
  int overDueStillWaiting;
};

tuple TPatientOutputDataBySurgery {
  key int period;
  key TSurgeries surgeryId;
  int cat;
  string department;
  int waiting;
  int incoming;
  int treated;
  int stillWaiting;
  int overDueWaiting;
  int overDueIncoming;
  int overDueTreated;
  int overDueStillWaiting;
};

tuple TPointAchievedForTarget {
  key int _0CostedCategoriesValue;
  key int _1NullValue;
  key int _2PointsValue;
  int value;
};

tuple TTargets {
  key int category;
  key int period;
  float targetValue;
  float currentValue;
};
{TBedStayCategories} bedStayCategories;
{TBeds} beds with departmentId in DEPARTMENTS;
{TDepartmentNames} departmentNames;
{TIcu} icu with departmentId in DEPARTMENTS;
{TNAddedSessions} nAddedSessions_result with _0DepartmentsId in DEPARTMENTS;
range TNAllocatedSessions__1CyclesValue_range = 1..fixedParams.basePlanCycleLength;
{TNAllocatedSessions} nAllocatedSessions_result with _0DepartmentsId in DEPARTMENTS, _1CyclesValue in TNAllocatedSessions__1CyclesValue_range;
range TNDeletedSessions__1CyclesValue_range = 1..fixedParams.basePlanCycleLength;
{TNDeletedSessions} nDeletedSessions_result with _0DepartmentsId in DEPARTMENTS, _1CyclesValue in TNDeletedSessions__1CyclesValue_range;
range TNPatientsTreatedResult_period_range = 1..baseConfigParams.nPeriods;
{TNPatientsTreatedResult} nPatientsTreatedResult with period in TNPatientsTreatedResult_period_range, surgeriesId in SURGERIES;
range TNTotalChanges__1CyclesValue_range = 1..fixedParams.basePlanCycleLength;
{TNTotalChanges} nTotalChanges with departmentId in DEPARTMENTS, _1CyclesValue in TNTotalChanges__1CyclesValue_range;
range TNTreatedPatientsOnTimePerPeriod__1PeriodsValue_range = 1..baseConfigParams.nPeriods;
{TNTreatedPatientsOnTimePerPeriod} nTreatedPatientsOnTimePerPeriod_result with _0SurgeriesId in SURGERIES, _1PeriodsValue in TNTreatedPatientsOnTimePerPeriod__1PeriodsValue_range;
range TNTreatedPatientsOverDuePerPeriod__1PeriodsValue_range = 1..baseConfigParams.nPeriods;
{TNTreatedPatientsOverDuePerPeriod} nTreatedPatientsOverDuePerPeriod_result with _0SurgeriesId in SURGERIES, _1PeriodsValue in TNTreatedPatientsOverDuePerPeriod__1PeriodsValue_range;
range TNTreatedPatients__1PeriodsValue_range = 1..baseConfigParams.nPeriods;
{TNTreatedPatients} nTreatedPatients_result with _0SurgeriesId in SURGERIES, _1PeriodsValue in TNTreatedPatients__1PeriodsValue_range;
{TPatientOutputData} patientOutputData;
range TPatientOutputDataBySurgery_period_range = 1..baseConfigParams.nPeriods;
{TPatientOutputDataBySurgery} patientOutputDataBySurgery with period in TPatientOutputDataBySurgery_period_range, surgeryId in SURGERIES;
range TPointAchievedForTarget__0CostedCategoriesValue_range = 2..fixedParams.nCategories;
range TPointAchievedForTarget__1NullValue_range = 1..fixedParams.nPerformanceMeasurements;
range TPointAchievedForTarget__2PointsValue_range = 1..fixedParams.nPoints;
{TPointAchievedForTarget} pointAchievedForTarget_result with _0CostedCategoriesValue in TPointAchievedForTarget__0CostedCategoriesValue_range, _1NullValue in TPointAchievedForTarget__1NullValue_range, _2PointsValue in TPointAchievedForTarget__2PointsValue_range;
{TTargets} targets;

tuple TBasePlanByName {
  key TDepartmentNames departmentName;
  key int period;
  int value;
};

tuple TBedsByName {
  key TDepartmentNames departmentName;
  key int period;
  float value;
};

tuple TBedsStatusByCategories {
  key TBedStayCategories categoryName;
  key int period;
  float value;
};

tuple TNAllocatedSessionsByNames {
  key TDepartmentNames departmentName;
  key int period;
  int value;
};

tuple TNTreatedPatientsByName {
  key TDepartmentNames departmentName;
  key TSurgeries surgeryId;
  key int period;
  int value;
};

tuple TSurgeriesByName {
  key int id;
  string surgeryName;
  TDepartmentNames departmentName;
  float duration;
  float los;
  float pIcu;
  int cat;
  float wies;
};

tuple TWiesEstimate {
  key TDepartmentNames departmentName;
  key int period;
  float value;
};
range TBasePlanByName_period_range = 1..28;
{TBasePlanByName} basePlanByName with departmentName in departmentNames, period in TBasePlanByName_period_range;
{TBedsByName} bedsByName with departmentName in departmentNames;
{TBedsStatusByCategories} bedsStatusByCategories with categoryName in bedStayCategories;
range TNAllocatedSessionsByNames_period_range = 1..28;
{TNAllocatedSessionsByNames} nAllocatedSessionsByNames with departmentName in departmentNames, period in TNAllocatedSessionsByNames_period_range;
{TNTreatedPatientsByName} nTreatedPatientsByName with departmentName in departmentNames, surgeryId in SURGERIES;
{TSurgeriesByName} surgeriesByName with departmentName in departmentNames;
{TWiesEstimate} wiesEstimate with departmentName in departmentNames;




//TODO (actually really more of a note)
//We should see if going a long way back causes significant computational yuckiness
//If it does so, we should both fix it, and buy Hamideh a hot chocolate for being right :)
//If it doesn't cause a big issue, Liv gets to choose - hot chocolate or boost (she chooses boost)

/*********************************************
 *********************************************
 * SECTION 1 - INPUT DATA AND SETUP 
 *********************************************
 *********************************************/

/*********************************************
 * timing values and ranges
 *********************************************/
int nPeriods = baseConfigParams.nPeriods; 			//number of periods for optimisation
int startPeriod = rollingTimeParams.startPeriod; 	// start period of current window
int endPeriod =  startPeriod + nPeriods - 1;		// end period of current window
range PERIODS = 1..endPeriod; 						//this starts from 1 not startPeriod for rolling time reasons
int nTotalPeriods = timingParams.nTotalPeriods; 	// number of periods with data
range TOTAL_PERIODS = 1..nTotalPeriods;				// all periods with data
range SINCE_SURGERY = 0..endPeriod - 1;				// the set of possible numbers of periods since surgery
int cycleLength = fixedParams.basePlanCycleLength;	// the number of periods per cycle

/*********************************************
 * non-timing index ranges 
 *********************************************/
int nCategories = fixedParams.nCategories;		// number of clinical urgency categories	
range CATEGORIES = 1..nCategories;				// the set of all clinical urgency categories
range COSTED_CATEGORIES = 2..nCategories;		// the set of clinical urgency categories for which performance is measured
range POINTS = 1..fixedParams.nPoints;			// the possible points for each KPI

/*********************************************
 * index ranges for "performance measurements" (e.g. quarters)
 *********************************************/
int nPerformanceMeasurements = fixedParams.nPerformanceMeasurements;// the number of times performance is measured for the whole time period, e.g. 4 to measure quarterly
range PERFORMANCE_MEASUREMENTS = 1..nPerformanceMeasurements;		// the set of times when performance is measured
int performanceMeasurementOfPeriod[TOTAL_PERIODS]; // which quarter does each period belong to
execute{
  var pm = 1;
  var changeAt = endDayOfPerformanceMeasurement[pm];
  for(var i = 1; i <= nTotalPeriods; i++){
    performanceMeasurementOfPeriod[i] = pm;
    if(i == changeAt){
      pm ++;
      if(pm <= nPerformanceMeasurements){
  	  changeAt = endDayOfPerformanceMeasurement[pm];}
    }      
  }    
}  
int firstMeasurementPeriod = performanceMeasurementOfPeriod[startPeriod]; //where do the "so far this measurement period" input patients go?

/*********************************************
 * Set for integer heuristics 
 *********************************************/
range PERIODS_INTEGER = 		//integer time periods
		optimisationParams.minIntegerPeriod..
		minl(optimisationParams.minIntegerPeriod + 
		optimisationParams.integerWindowSize - 1,endPeriod);

/*********************************************
 * Sets for rolling time heuristics 
 *********************************************/
{int} BASE_PLAN_CONSTRAINT_START_PERIODS = {startPeriod}; //some constraints have to be repeated
//execute{
//  var day = startPeriod;
//  while(day > 0 && startPeriod - day < nPeriods){
//    BASE_PLAN_CONSTRAINT_START_PERIODS.add(day);
//    day -= rollingTimeParams.rollingTimeStepSize;
//  }    
//}
//tuple TFixedTreatments{					//for fixing treatments
//  int surgeryId;
//  int period;
//  int nTreated;
//};  
//{TFixedTreatments} FIXED_TREATMENTS={}; //initially empty - filled as fixes are made
//  
		
/*********************************************
 * optimisation parameters 
 *********************************************/
int timeLimit = optimisationParams.timeLimit;				//The time limit per iteration
float gap = optimisationParams.gap;							//The optimality gap required
int allowEmptySessions= scenarioParams.allowEmptySessions; 	// 0 forbids empty sessions, anything else allows them
string runName = scenarioParams.runName;					//The name of the run for reporting purposes
int minPoints = objFunParams.minPoints;						//The minimum number of KPI points required
int allowCat1Violation = scenarioParams.allowCat1Violation;	//if 0 then cat 1 constraints are hard, otherwise can be violated

/*********************************************
 * patient data for ODME outputs
 *********************************************/
range PERIODS_LEFT = 0..max(c in CATEGORIES) maxWaitListStay[c];	//for indexing timing of patients for output
range PERIODS_PLUS = startPeriod..endPeriod+1;						//for indexing where we need endPeriod + 1
int nPatientsWithDaysLeft[PERIODS_PLUS,SURGERIES,PERIODS_LEFT];		// output - what does the waiting list look like at each period?
int nPatientsTreatedWithDaysLeft[PERIODS,SURGERIES,PERIODS_LEFT];	// output - which patients were treated in each period?
int nPatientsTreatedOnTime[PERIODS, SURGERIES];						// output - how many of the treated patients were on time?
int nPatientsTreatedOverDue[PERIODS, SURGERIES];					// output - how many of the treated patients were over due?

/*********************************************
 * bed management
 *********************************************/
float bedStayPeriodsAfterSurgery[SURGERIES, SINCE_SURGERY] = 		// for a given number of days after surgery, how much bed time is required	
	[ s: [ p : maxl(minl(s.los - p,1),0)| p in SINCE_SURGERY] 
		| s in SURGERIES];

/*********************************************
 * base plan - how many sessions are allocated on each day
 *********************************************/
int offset = (startPeriod - 1) mod cycleLength; //when is the start day?
int equivalentBasePlanPeriod[p in PERIODS] = 	//finding base plan period for each actual period (avoiding negatives)
		(p + cycleLength *nTotalPeriods - offset) mod cycleLength; 
execute{										//correcting for 0
 	for(var p in PERIODS)
 	{
   		if (equivalentBasePlanPeriod[p]==0){
   		   equivalentBasePlanPeriod[p] = cycleLength;  
        }   		  
 	}   
};  
int nAllocatedSessionsBasePlan[DEPARTMENTS][PERIODS] = //what does the base plan mean for each actual period
		[ d: [ p : sum(<id,equivalentBasePlanPeriod[p],n> in BASE_PLAN_INFO : id == d) n | p in PERIODS]
		| d in DEPARTMENTS];
		
		execute{
		writeln(nAllocatedSessionsBasePlan);		
		}

/*********************************************
 * constants
 *********************************************/
int maxAdditions = scenarioParams.maxAdditions;				//maximum total added sessions
int maxDeletions = scenarioParams.maxDeletions;				//maximum total deleted sessions
int maxChanges = scenarioParams.maxChanges;					//maximum total changes (additions + deletions)
int maxTotalSessions = scenarioParams.maxTotalSessions;		//total funded sessions for whole planning horizon      
int nTotalOTSessions=scenarioParams.nTotalOtsessions;		//The total number of operating theatre sessions per period
int nWorkingDays=timingParams.nWorkingDays;					//The number of working days per period (e.g. 5 for weekly)
int sessionLength=baseConfigParams.sessionLength;			// the length of the session in the same units as durations in the surgeries
int nICUPatientsPerDay=scenarioParams.nIcupatientsPerDay;	// the maximum number of patients who can be sent to ICU per day

/*********************************************
 * setting up the patient numbers
 *********************************************/
range PAST_PERIODS = -max(c in CATEGORIES) maxWaitListStay[c]..0;	//how long could people have been waiting
int nInitialPatients[SURGERIES, PAST_PERIODS] = 					//initial patients
	[s : [-maxWaitListStay[s.cat] + pl: n] | s in SURGERIES, <s.id,pl,n> in initialPatientInfo];
int nArrivingPatients[SURGERIES, TOTAL_PERIODS] = 					//arriving patients
	[s : [p : n] | s in SURGERIES, <s.id,p,n> in arrivingPatientInfo];

/*********************************************
 * Data for Big Ms
 *********************************************/
float objM[c in COSTED_CATEGORIES, q in PERFORMANCE_MEASUREMENTS, 
   			point in POINTS] =
	fractionForPoint[c,q,point] *
	sum(s in SURGERIES : s.cat == c)(
	  	sum(p in PAST_PERIODS )
	  	  	nInitialPatients[s,p] +
	  	sum(p in PERIODS )
	  	  	nArrivingPatients[s,p] 
  		+ nOverDueBeforeHorizon[c] + nOnTimeBeforeHorizon[c]
  		);	  	
			
			
/*********************************************
 *********************************************
 * SECTION 2 - VARIABLES
 *********************************************
 *********************************************/
 
/*********************************************
 * session variables
 *********************************************/
dvar int+ nAllocatedSessions[DEPARTMENTS, PERIODS]; 	//called X in the model
dvar float+ nDeletedSessions[DEPARTMENTS, PERIODS];		
dvar float+ nAddedSessions[DEPARTMENTS, PERIODS];		

/*********************************************
 * treated patient variables
 *********************************************/
dvar float+ nTreatedPatients[SURGERIES, PERIODS];  		//called K in the model
dvar int+ integerTreatedPatients[SURGERIES, PERIODS];   //only for casting purposes

/*********************************************
 * treated patients - mapping what happens throughout
 *********************************************/
dvar float+ nTreatedPatientsOnTimePerPeriod[SURGERIES, PERIODS];    //called \theta_{s,p}^+ in the model 
dvar float+ nTreatedPatientsOverDuePerPeriod[SURGERIES, PERIODS]; 	//called \theta_{s,p}^- in the model 
dvar float+ nWaitingPatientsOnTime[SURGERIES, PERIODS]; 			//at END of period - called \Omega_{s,p}^+ in the model
dvar float+ nWaitingPatientsOverDue[SURGERIES, PERIODS]; 			//at END of period - called \Omega_{s,p}^- in the model
dvar float+ nWaitingPatientsOverDueAtPeriodStart[SURGERIES, PERIODS]; //called \Omega_{s,p} in the model

/*********************************************
 * measuring how many points were acheived based on how many
 * patients were treated on time vs overdue
 *********************************************/
dvar int pointAchievedForTarget[COSTED_CATEGORIES, 1..nPerformanceMeasurements, POINTS] in 0..1; //called y in the model
dvar float+ nTreatedPatientsOnTime[CATEGORIES, 1..nPerformanceMeasurements];  //called $\Pi^+$ in the model  Note: not needed in the short model (for patent)
dvar float+ nTreatedPatientsOverDue[CATEGORIES, 1..nPerformanceMeasurements]; //called $\Pi^-$ in the model  Note: not needed in the short model (for patent)


/*********************************************
 * variables to control constrain violation wrt cat 1
 *********************************************/
dvar float+ cat1TreatedLeftovers[SURGERIES, PERIODS]; 	// \sigma_{s,p}^+ in the model
dvar float+ cat1WaitListLeftovers[SURGERIES, PERIODS]; 	// \sigma_{s,p}^- in the model 

/*********************************************
 * measuring what's left on the waiting list
 *********************************************/
dvar float durationWorkload;	//remaining workload in terms of predicted surgery durations
dvar float bedWorkload;			//remaining workload in terms of predicted bed nights
dvar float workloadImbalance;	//imbalance in remaining workload

/*********************************************
 *********************************************
 * SECTION 3 - OBJECTIVE FUNCTION
 *********************************************
 *********************************************/

// maximise the number of points achieved, with a penalty applied for patients left on the waiting list
// there is a higher penalty for patients who are left on the list and are overdue

dexpr float points =  sum( i in COSTED_CATEGORIES, q in PERFORMANCE_MEASUREMENTS, j in POINTS) 
					pointAchievedForTarget[i,q,j];

dexpr float overdue =  sum(s in SURGERIES) nWaitingPatientsOverDue[s, endPeriod];				
dexpr float ontime =  sum(s in SURGERIES) nWaitingPatientsOnTime[s, endPeriod];
dexpr float cat1 =  sum(s in SURGERIES, p in PERIODS) 
		 			(cat1TreatedLeftovers[s,p] + cat1WaitListLeftovers[s,p]) ;
dexpr float durationExpr = durationWorkload;
dexpr float bedExpr = bedWorkload;
dexpr float imbalanceExpr = workloadImbalance;

maximize 1.0 * points - 
		 1* overdue - 
		 1* ontime -
		 1* cat1 -
		 1* durationExpr - 
		 1* bedExpr - 
		 1* imbalanceExpr; 


/*********************************************
 *********************************************
 * SECTION 4 - CONSTRAINTS
 *********************************************
 *********************************************/
subject to
{ 
	/*********************************************
	 * objective function - set workloads
	 *********************************************/
 	ctDurationWorkload :
	 	durationWorkload == sum(s in SURGERIES) ((nWaitingPatientsOverDue[s, endPeriod] + 
 			nWaitingPatientsOnTime[s, endPeriod]) * s.duration) / objFunParams.durationPerWorkload;
 	ctBedWorkload :
	 	bedWorkload == sum(s in SURGERIES) ((nWaitingPatientsOverDue[s, endPeriod] + 
 			nWaitingPatientsOnTime[s, endPeriod]) * s.los) / objFunParams.bedsPerWorkload;
 	ctWorkloadImbalance1 :
 	  	workloadImbalance >= durationWorkload - bedWorkload;		
 	ctWorkloadImbalance2 :
 	  	workloadImbalance >= bedWorkload - durationWorkload;		

	/*********************************************
	 * performance point related constraints
	 *********************************************/
 	ctMinPerformancePoints:	
	 	sum( i in COSTED_CATEGORIES, q in PERFORMANCE_MEASUREMENTS, j in POINTS) 
					pointAchievedForTarget[i,q,j] >= minPoints;

	forall (c in COSTED_CATEGORIES, q in PERFORMANCE_MEASUREMENTS){
	   	objectiveConstraints_onTime:// Set the nTreatedPatientsOnTime variables
	    	nTreatedPatientsOnTime[c,q] == 
		      	sum(qq in PERFORMANCE_MEASUREMENTS : qq == q && qq == firstMeasurementPeriod)
		      	  	nOnTimeBeforeHorizon[c] +
	    		sum(p in PERIODS : performanceMeasurementOfPeriod[p] == q,
	    			 	s in SURGERIES : s.cat == c)
	    		  	nTreatedPatientsOnTimePerPeriod[s][p];

	   	objectiveConstraints_overDue:// Set the nTreatedPatientsOverDue variables
	      	nTreatedPatientsOverDue[c,q] == 
	      	sum(qq in PERFORMANCE_MEASUREMENTS : qq == q && qq == firstMeasurementPeriod)
	      	  	nOverDueBeforeHorizon[c] +
	      	sum(p in PERIODS: performanceMeasurementOfPeriod[p]== q, 
	      			s in SURGERIES: s.cat==c) 
	      		nTreatedPatientsOverDuePerPeriod[s, p];

	   	forall (point in POINTS){
	     	objectiveConstraints_setPoints:// To get points, you must reach the required fraction of on time patients
	   			nTreatedPatientsOnTime[c,q] >= 
	   				fractionForPoint[c,q,point] * 
	   					(nTreatedPatientsOnTime[c,q] + 
	   					 nTreatedPatientsOverDue[c,q]) 
	   				- objM[c,q,point]*(1- pointAchievedForTarget[c,q,point]);
	
	 	}  
 	}	 	
	
  
	/*********************************************
	 * capacity constraints
	 *********************************************/
    forall (p in PERIODS, d in DEPARTMENTS){
    	ctLinkSurgeryTypeToSurgeySession: // upper bound on surgery time given allocated sessions
       		sum(s in SURGERIES : s.departmentID==d) nTreatedPatients[s,p]*s.duration <= 
       			sessionLength * nAllocatedSessions[d,p]; 

        if(allowEmptySessions==0){
       		ctEliminateEmptySessions: // lower bound on surgery time given allocated sessions
       			sum(s in SURGERIES : s.departmentID==d) nTreatedPatients[s,p]*s.duration - 1 >= 
       			sessionLength * (nAllocatedSessions[d,p]-1);
        }       			
    }           

    forall (p in PERIODS, <id, p, n> in WARD_INFO){	
     	ctBedAvailability: // total bed usage
        	sum (s in SURGERIES: s.wardID==id) sum(past in 0..(p-startPeriod))
        		nTreatedPatients[s,p-past]*bedStayPeriodsAfterSurgery[s][past] <= n;
    }        
 	    
 	forall(p in PERIODS){
      	ctICUAvailability: // ICU usage per day
	        sum(s in SURGERIES) nTreatedPatients[s,p]*(s.pICU) <= 
	        	nWorkingDays * nICUPatientsPerDay;
    }          
 	
 	forall (d in DEPARTMENTS, p in PERIODS){
    	ctMaxSessionsForSurgeryDepartment: // per department, maximum sessions per period
          	nAllocatedSessions[d, p] <= d.maxSessionsPerPeriod;
    }
 	
    forall (p in PERIODS){
      	ctMaxSessionsForOTs: //maximum session for all OT
     		sum(d in DEPARTMENTS) nAllocatedSessions[d, p] <= nTotalOTSessions;
    }
 	
 	forall (p in PERIODS, <id,p,av> in SPECIALIST_INFO){
      	ctSpecAvailability: //specialist availability
	        sum(s in SURGERIES: s.specialistID == id)
	          	nTreatedPatients[s,p] * s.duration <=
	        av * sessionLength;
    }  
    

	/*********************************************
	 * base plan and allocated sessions
	 *********************************************/
    forall(p in PERIODS, d in DEPARTMENTS) {
     	ctLinkAllocatedSessionsToBasePlan: // calculates added and deleted sessions
     	  	nAllocatedSessionsBasePlan[d, equivalentBasePlanPeriod[p]] 
     	  	- nDeletedSessions[d,p] 
     	  	+ nAddedSessions[d,p]
     	  	== nAllocatedSessions[d,p]; 
    }      

    forall(myStart in BASE_PLAN_CONSTRAINT_START_PERIODS){//applied as the rolling time horizon rolls
	    ctMaxAdditions ://upper bound on total number of additions
	      	sum(d in DEPARTMENTS, p in myStart..myStart+nPeriods-1) nAddedSessions[d,p] <= maxAdditions;
	      	
	    ctMaxDeletions ://upper bound on total number of deletions
	      	sum(d in DEPARTMENTS, p in  myStart..myStart+nPeriods-1) nDeletedSessions[d,p] <= maxDeletions;
	      	
//	    ctMaxChanges ://upper bound on total number of changes
//	      	sum(d in DEPARTMENTS, p in myStart..myStart+nPeriods-1) nDeletedSessions[d,p] +
//	      		sum(d in DEPARTMENTS, p in myStart..myStart+nPeriods-1) nAddedSessions[d,p] <= maxChanges;
	      	
	    ctMaxSessions://Limit on the total number of sessions
	      	sum(d in DEPARTMENTS, p in  myStart..myStart+nPeriods-1) nAllocatedSessions[d,p] <= maxTotalSessions;
 	}	    

	/*********************************************
	 * constrain fixed variables for rolling time horizon
	 *********************************************/
 	forall(d in DEPARTMENTS, <d.id,p,v> in allocatedSessionsFixed){
 	  ctFixAllocatedSessions:	//allocated sessions
 	    nAllocatedSessions[d][p] == v;
    }
    forall(s in SURGERIES, <s.id, p,v> in treatedPatientsFixed){
     ctFixTreatedPatients:		//number of treated patients
       nTreatedPatients[s][p]==v; 
    }      
    
	/*********************************************
	 * cat 1 patients
	 *********************************************/
    forall (s in SURGERIES: s.cat == 1, p in PERIODS){
      	if(p > 1){
	      	ctUrgencyCat1Treatment://can't treat cat 1's overdue
	        	nTreatedPatientsOverDuePerPeriod[s, p] - cat1TreatedLeftovers[s,p] == 0;
        }	        	
       	ctUrgencyCat1WaitList://can't have overdue cat 1's on the waiting list
          	nWaitingPatientsOverDue[s, p] - cat1WaitListLeftovers[s,p] == 0;
    }  
              
    if(allowCat1Violation == 0){
	    ctCat1PatientsMustBeTreated: // set the slack variables to 0
	 	sum(s in SURGERIES, p in PERIODS: p > 1) 
			(cat1TreatedLeftovers[s,p] + cat1WaitListLeftovers[s,p])== 0;
	}		
 	
	/*********************************************
	 * mapping the patient flow
	 *********************************************/
   	forall(s in SURGERIES, p in PERIODS){
    	nWaitingPatientsOverDue[s][p] >= //waiting overdue >= overdue arrived - total treated 
    		sum(pOld in PAST_PERIODS : (p - pOld) >= maxWaitListStay[s.cat] ) nInitialPatients[s,pOld] +
    		sum(pOld in PERIODS : (p - pOld) >= maxWaitListStay[s.cat] ) nArrivingPatients[s,pOld] -
    		sum(pOld in PERIODS : pOld <= p) (nTreatedPatients[s,pOld]);
		
    	nWaitingPatientsOnTime[s][p] == //waiting on time = total arrivals - total treated - waiting overdue
    		sum(pOld in PAST_PERIODS : pOld <= p ) nInitialPatients[s,pOld] +
    		sum(pOld in PERIODS : pOld <= p ) nArrivingPatients[s,pOld] -
    		sum(pOld in PERIODS : pOld <= p) (nTreatedPatients[s,pOld]) - 
    		nWaitingPatientsOverDue[s][p];
    	
    	nWaitingPatientsOverDueAtPeriodStart[s][p] >= //initial overdue at start of p >= overdue arrived - treated before p
    		sum(pOld in PAST_PERIODS : (p - pOld) >= maxWaitListStay[s.cat] ) nInitialPatients[s,pOld] +
    		sum(pOld in PERIODS : (p - pOld) >= maxWaitListStay[s.cat] ) nArrivingPatients[s,pOld] -
    		sum(pOld in PERIODS : pOld < p) (nTreatedPatients[s,pOld]);
    	
		nTreatedPatientsOverDuePerPeriod[s][p] == //treated overdue in p
			nWaitingPatientsOverDueAtPeriodStart[s][p] - nWaitingPatientsOverDue[s][p];
		
		nTreatedPatientsOnTimePerPeriod[s][p] == //treated on time in p
			nTreatedPatients[s][p] - nTreatedPatientsOverDuePerPeriod[s][p];
  	}    
 
 	forall(s in SURGERIES, p in PERIODS_INTEGER){
		ctIntegerTreat: //require integrality as appropriate
		  	integerTreatedPatients[s,p] == nTreatedPatients[s,p];
  	}		  
}
 
/*********************************************
 *********************************************
 * SECTION 5 - OUTPUT DATA FOR ODME
 *********************************************
 *********************************************/

 execute ODME_DATA{
/*********************************************
 *********************************************
 * SECTION 5.1 - DIRECTLY MEASURE PATIENTS 
 *********************************************
 *********************************************/
   
	/*********************************************
	 * nPatientsWithDaysLeft - set up period 1
	 *********************************************/
	for(var s in SURGERIES){
	   	for(var pl in PERIODS_LEFT){ // base case
	     	nPatientsWithDaysLeft[1][s][pl] = 0; 
	   	}     
	}   
 	for(var s in SURGERIES){
   		for(var ipi in initialPatientInfo){
     		if(s.id == ipi.surgeryID){
       			if(ipi.periodsLeft == 0){ // patient is still overdue on period 1
         			nPatientsWithDaysLeft[1][s][ipi.periodsLeft] += ipi.nPatients;
       			} 
       			else{ // patient has one fewer periods left on period 1
    	     		nPatientsWithDaysLeft[1][s][ipi.periodsLeft - 1] += ipi.nPatients;
       			}       
     		}     
   		}     
   		for(var api in arrivingPatientInfo){
     		if(s.id == api.surgeryID){
       			if(api.period == 1){ //add in patients who arrive in period 1
         			nPatientsWithDaysLeft[1][s][maxWaitListStay[s.cat]] = api.nPatients;
       			}         
     		}       
   		}     
 	}   
 
	/*********************************************
	 * update data as patients are treated
	 *********************************************/
 	for(var p in PERIODS){	// important to update in period order
   		for(var s in SURGERIES){
     		var maxStay = maxWaitListStay[s.cat];
     		//nPatientsLeft is the number who still need to be allocated as treated
     		var nPatientsLeft = Opl.round(nTreatedPatients[s][p]); 
     		//treat in turn - first treat as many overdue as possible
     		nPatientsTreatedOverDue[p][s] = Opl.minl(nPatientsWithDaysLeft[p][s][0],nPatientsLeft);
     		//all those treated who weren't overdue were on time
     		nPatientsTreatedOnTime[p][s] = nPatientsLeft - nPatientsTreatedOverDue[p][s];
     		//mark the treated patients
     		for(var pl in PERIODS_LEFT){
       			if(nPatientsLeft > 0){ //work through the values of PERIODS_LEFT, treating in turn
	       			nPatientsTreatedWithDaysLeft[p][s][pl] = 
	       				Opl.minl(nPatientsWithDaysLeft[p][s][pl],nPatientsLeft);
	   	   			nPatientsLeft -= nPatientsTreatedWithDaysLeft[p][s][pl];    
       			}	
     		}
     		//update who is left on the list (taking out nPatientsTreatedWithDaysLeft)
     		for(var pl in PERIODS_LEFT){              
       			if(pl == 0){ //overdue in the next period is existing overdue - treated overdue 
       						 // + (1 period left - 1 period left treated)
       	   			nPatientsWithDaysLeft[p+1][s][pl] = nPatientsWithDaysLeft[p][s][pl] - 
       	   					nPatientsTreatedWithDaysLeft[p][s][pl] + 
       	   					nPatientsWithDaysLeft[p][s][pl+1] - 
       	   					nPatientsTreatedWithDaysLeft[p][s][pl+1];
	       		}         
	       		else if(pl < maxStay){ //next period with pl = this period with pl + 1 - treated
	       	   		nPatientsWithDaysLeft[p+1][s][pl] = nPatientsWithDaysLeft[p][s][pl+1] - 
	       	   				nPatientsTreatedWithDaysLeft[p][s][pl+1]; 
	       		}    
	       		else if(pl == maxStay && p < endPeriod){ // for max wait list stay, it's the arriving patients
	           		nPatientsWithDaysLeft[p+1][s][pl] = nArrivingPatients[s][p+1];
	       		}              
	     	}        
	   	}     
 	}   
 	
	/*********************************************
	 * patient output data - how many patients are 
	 * doing what in each period
	 * also writes the output
	 *********************************************/
 	for(var p in PERIODS){
    	var numWait = 0;
    	var numWaitOverDue = 0;
		var incoming = 0;
    	var incomingOverDue = 0;
		var treated = 0;
		var treatedOverDue = 0;
		var stillWaitng = 0;
		var stillWaitingOverDue = 0;
	
		for(var s in SURGERIES){
		  	//initialise some
		    var numWaitSurgery = 0;
			var stillWaitngSurgery = 0;
			var stillWaitingOverDueSurgery = 0;
	    	var numWaitOverDueSurgery = nPatientsWithDaysLeft[p][s][0];
			var incomingSurgery = nArrivingPatients[s][p];
			//set the simple parameters
			var treatedSurgery = nPatientsTreatedOnTime[p][s] + 
					nPatientsTreatedOverDue[p][s];
			var treatedOverDueSurgery = nPatientsTreatedOverDue[p][s];
			
			if( p > 1){
				//incoming overdue = those who had 1 period left last period and didn't get treated, 
		  		incomingOverDueSurgery = Opl.maxl(0,
		  			nPatientsWithDaysLeft[p-1][s][1] - nPatientsTreatedOnTime[p-1][s]);
			}
			else{
			  	//incoming overdue = those who initially had one period left
	    		incomingOverDueSurgery = nInitialPatients[s][-maxWaitListStay[s.cat] + 1];
  			}		  
		
			for(var pl = 0; pl <= maxWaitListStay[s.cat]; pl++){
			  	//number waiting = number waiting with any days left
				numWaitSurgery += nPatientsWithDaysLeft[p][s][pl];  
  			}		  
		
			//the nPatientsWithDaysLeft values include the new arrivals, subtract these out
			numWaitOverDueSurgery -= incomingOverDueSurgery;
			numWaitSurgery -= incomingSurgery;
			
			//calculate waiting numbers
			stillWaitngSurgery = numWaitSurgery + incomingSurgery - treatedSurgery;
			stillWaitingOverDueSurgery = numWaitOverDueSurgery + incomingOverDueSurgery - treatedOverDueSurgery;
			
			//add surgery specific values to total 
	    	numWait += numWaitSurgery;
	    	numWaitOverDue += numWaitOverDueSurgery;
			incoming += incomingSurgery;
	    	incomingOverDue += incomingOverDueSurgery;
			treated += treatedSurgery;
			treatedOverDue += treatedOverDueSurgery;
			stillWaitng += stillWaitngSurgery;
			stillWaitingOverDue += stillWaitingOverDueSurgery;
			patientOutputDataBySurgery.add(p, s.id, s.cat, s.departmentID.name, numWaitSurgery, incomingSurgery, treatedSurgery, 
			stillWaitngSurgery, numWaitOverDueSurgery, incomingOverDueSurgery, treatedOverDueSurgery, stillWaitingOverDueSurgery);  
	    
	    }   	
   	
    	patientOutputData.add(p, numWait, incoming, treated, stillWaitng, numWaitOverDue, incomingOverDue, treatedOverDue, stillWaitingOverDue);  
  	}  

/*********************************************
 *********************************************
 * SECTION 5.2 - WRITE OUTPUT DATA FOR ODME
 * NOTE THAT SOME OF THESE ARE FOR CHART MAKING
 *********************************************
 *********************************************/
   
	/*********************************************
	 * treated patients
	 *********************************************/
 	for(var p in PERIODS){
   		for(var s in SURGERIES){
     		for(var pl in PERIODS_LEFT){
       			if(nPatientsTreatedWithDaysLeft[p][s][pl] >= 0.1){
         			nPatientsTreatedResult.add(p,s,pl,Opl.round(nPatientsTreatedWithDaysLeft[p][s][pl]));
       			}         
     		}       
   		}     
 	}   
  
 	for(var d in DEPARTMENTS){
 		departmentNames.add(d.name, d.id);
 	}   
      
 	for(var d in DEPARTMENTS){
	    for(var p in PERIODS){ 
      		var nChanges = 0;
        	nAddedSessions_result.add(d,p,Opl.round(nAddedSessions[d][p]));
        	nChanges += Opl.round(nAddedSessions[d][p]);

	        nAllocatedSessions_result.add(d,p,Opl.round(nAllocatedSessions[d][p]),d.name); 
    	    nAllocatedSessionsByNames.add(d.name,p,Opl.round(nAllocatedSessions[d][p])); 

        	nDeletedSessions_result.add(d,p,Opl.round(nDeletedSessions[d][p]));
        	nChanges += Opl.round(nDeletedSessions[d][p]);

        	nTotalChanges.add(d,p,nChanges);
        	basePlanByName.add(d.name,p,Opl.round(nAllocatedSessionsBasePlan[d][p]));
    	}
  	}  
  
 
      
 //Hamideh added for the chart purpose 140521
 
 
 	for(var s in SURGERIES){
  		surgeriesByName.add(s.id, s.name, s.departmentID.name, s.duration, s.los, s.pICU, s.cat, s.wies); 
 	}   
 
 
 // Hamideh added for the planner 140521 

 	for(var s in SURGERIES){
  		for(var p in PERIODS){
    		if(nTreatedPatients[s][p]>0.1){
    			nTreatedPatientsByName.add(s.departmentID.name, s.id, p, Opl.round(nTreatedPatients[s][p]));
  			}     
  		}     
 	}   


// Hamideh added for the planner 140522

 	for(var d in DEPARTMENTS){
  		for (var p in PERIODS){
   			var tmp=0.0;
   			var count=0;
   			for(var s in SURGERIES){
     			if(s.departmentID == d){
    				tmp+= Opl.round(nTreatedPatients[s][p])* s.wies;
    				count+=1;
      			}    	
    		} 
   			wiesEstimate.add(d.name,p, tmp);     
  		}     
 	}  
 
  	for(var s in SURGERIES){
	    for(var p in PERIODS){
      		if(nTreatedPatients[s][p] >= 0.1){
        		nTreatedPatients_result.add(s,p,Opl.round(nTreatedPatients[s][p]));
        
	    	}       
    	  	if(nPatientsTreatedOverDue[p][s] >= 0.1){
        		nTreatedPatientsOverDuePerPeriod_result.add(s,p,Opl.round(nPatientsTreatedOverDue[p][s]));
      		} 
      		if(nPatientsTreatedOnTime[p][s] >= 0.1){
        		nTreatedPatientsOnTimePerPeriod_result.add(s,p,Opl.round(nPatientsTreatedOnTime[p][s]));
      		} 
    	}        
  	}
  
	for(var c in COSTED_CATEGORIES){
    	for(var q in PERFORMANCE_MEASUREMENTS){
      		for(var p in POINTS){
        		pointAchievedForTarget_result.add(c,q,p,Opl.round(pointAchievedForTarget[c][q][p]));
      		}        
    	}      
  	}  
  

  
  	for(var p in PERIODS){
    	for( var d in DEPARTMENTS){
      		var bedUse = 0;
      		var icuUse = 0;
      		for(var s in SURGERIES){
        		if(s.departmentID == d){
		  			for(var past = 0; past <= p - 1; past++){
	          			bedUse += bedStayPeriodsAfterSurgery[s][past] * nTreatedPatients[s][p-past];
          			}	          
          			icuUse += s.pICU * nTreatedPatients[s][p];
        		}          
      		}
      		beds.add(d,p,bedUse);
      		icu.add(d,p,icuUse);   
      		bedsByName.add(d.name, p, bedUse);     
    	}      
  	}    
  
  	bedStayCategories.add("dayStay");
  	bedStayCategories.add("overNight");
  	bedStayCategories.add("multiNights");
  
	for(var p in PERIODS){
    	var dayStayCounter=0;
    	var overNightCounter=0;
    	var multiNightsCounter=0;
    	for (var s in SURGERIES){
     		if(s.los<1){
       			for(var past = 0; past <= p - 1; past++){
	           		dayStayCounter+= bedStayPeriodsAfterSurgery[s][past] * nTreatedPatients[s][p-past];
        		}	 
      		}       
     		if(s.los>=1 & s.los<2){
       			for(var past = 0; past <= p - 1; past++){
	          		overNightCounter+= bedStayPeriodsAfterSurgery[s][past] * nTreatedPatients[s][p-past];
        		}	 
      		} 
      
      		if(s.los>=2 ){
       			for(var past = 0; past <= p - 1; past++){
	          		multiNightsCounter+= bedStayPeriodsAfterSurgery[s][past] *  nTreatedPatients[s][p-past];
        		}	 
      		}
    	}       
     	bedsStatusByCategories.add("dayStay",p,dayStayCounter);
     	bedsStatusByCategories.add("overNight",p,overNightCounter);
     	bedsStatusByCategories.add("multiNights",p,multiNightsCounter);
        
	}    

  	for(var pm in PERFORMANCE_MEASUREMENTS){
      	var target = 1;
      	var actual = 1;
      	if(nTreatedPatientsOverDue[c][pm] > 0){
        	actual = nTreatedPatientsOnTime[c][pm] / (nTreatedPatientsOnTime[c][pm] + nTreatedPatientsOnTime[c][pm]);
      	}    
      	targets.add(1,pm,target,actual);    
  	}      
  	for(var c in COSTED_CATEGORIES){
    	for(var pm in PERFORMANCE_MEASUREMENTS){
      		var target = fractionForPoint[c][pm][3];
      		var actual = 1;
      		if(nTreatedPatientsOverDue[c][pm] > 0){
        		actual = nTreatedPatientsOnTime[c][pm] / (nTreatedPatientsOnTime[c][pm] + nTreatedPatientsOnTime[c][pm]);
      		}    
      		targets.add(c,pm,target,actual);    
    	}      
  	}    
  
}  
