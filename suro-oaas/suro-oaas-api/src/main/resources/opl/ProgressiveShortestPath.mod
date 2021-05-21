/*********************************************
 * OPL 12.5 Model
 * Author: Administrator
 * Creation Date: Jun 27, 2013 at 1:30:56 PM
 *********************************************/

tuple Params {
  int s;
  int t;
};

Params params = ...;

tuple Tarc {
  int i;
  int j;
  int c;
};

setof(Tarc) existingArcs = ...;
setof(Tarc) possibleArcs = ...;
setof(Tarc) allArcs = existingArcs union possibleArcs;

int s = params.s;
int t = params.t;

setof(int) nodes = {i | <i,j,c> in allArcs} union {j | <i,j,c> in allArcs}; 
int val[nodes] = [i: 0 | i in nodes];

execute{
  val[s] = -1;
  val[t] = 1;
}  

int nTimes = card(possibleArcs);
range allTimes = 1..nTimes;
range extraTimes = 0..nTimes;
range nonFirstTimes = 2..nTimes;

dvar float+ x[allArcs, allTimes] in 0..1;
dvar int+ y[possibleArcs, extraTimes] in 0..1;
dvar int+ z[possibleArcs, extraTimes] in 0..1;
dexpr float totalValue = sum(a in allArcs, t in allTimes) a.c * x[a,t];

minimize totalValue;

subject to{
  forall(t in nonFirstTimes){
    forall(a in possibleArcs){
      y[a,t] >= y[a,t-1];
//      z[a,t] == y[a,t] - y[a,t-1];
    }      
  }
//  forall(t in nonFirstTimes){
//    sum(a in possibleArcs) z[a,t] == 1;
//  }    
//  forall(a in possibleArcs){
//    sum(t in nonFirstTimes) z[a,t] == 1;
//  }    
  forall(t in allTimes){
    sum(a in possibleArcs) (y[a,t] - y[a,t-1]) <= 1;
    forall(a in possibleArcs){
      x[a,t] <= y[a,t];
    }      
    forall(j in nodes){
      sum(<i,j,c> in allArcs) x[<i,j,c>,t] - sum(<j,k,c> in allArcs) x[<j,k,c>,t] == val[j];
    }      
  }
  forall(a in possibleArcs){
    y[a,0] == 0; 
  }            
}  

tuple value {
  float totalVal;
};
value Value = <totalValue>;

tuple usedWithTime {
  int i;
  int j;
  int c;
  int t;
  //float amount;
};
{usedWithTime} UsedWithTime = {< a.i, a.j, a.c, t/*, x[<a.i,a.j,a.c>,t]*/> | t in allTimes, a in possibleArcs : x[a][t] >= 0.5 };

execute{
  Value;
  UsedWithTime;
}  