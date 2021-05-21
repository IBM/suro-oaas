package com.ibm.au.optim.suro.model.store.domain.ingestion;

import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanEntry;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanList;

/**
 * Interface <b>BasePlanRepository</b>. This interface specialises {@link TemporalRepository}
 * to provide access to {@link BasePlanList} entities.
 *
 * @author Brendan Haesler
 */

public interface BasePlanListRepository
				extends TemporalRepository<BasePlanEntry, BasePlanList> {

	/**
	 * A {@link String} constant containing the name of the attribute that specifies the
	 * instance of the {@link BasePlanListRepository} implementation that has been injected into
	 * the environment.
	 */
	String BASE_PLAN_REPOSITORY_INSTANCE = "repo:baseplan:instance";

	/**
	 * A {@link String} constant containing the name type of {@link BasePlanListRepository} that
	 * will be used in the application.
	 */
	String BASE_PLAN_REPOSITORY_TYPE = BasePlanListRepository.class.getName();
}
