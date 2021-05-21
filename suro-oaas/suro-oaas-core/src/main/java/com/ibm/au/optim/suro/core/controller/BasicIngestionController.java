package com.ibm.au.optim.suro.core.controller;

import com.ibm.au.optim.suro.model.control.domain.ingestion.IngestionController;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanEntry;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.BasePlanList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.IcuAvailabilityList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.SpecialistAvailabilityList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.TemporalList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatient;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WaitingPatientList;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailability;
import com.ibm.au.optim.suro.model.entities.domain.ingestion.WardAvailabilityList;
import com.ibm.au.optim.suro.model.impl.AbstractSuroService;
import com.ibm.au.optim.suro.model.store.domain.ingestion.BasePlanListRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.IcuAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.SpecialistAvailabilitiesRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.TemporalRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.WaitingPatientListRepository;
import com.ibm.au.optim.suro.model.store.domain.ingestion.WardAvailabilitiesRepository;
import com.ibm.au.jaws.data.utils.ReflectionUtils;
import com.ibm.au.jaws.web.core.runtime.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the basic implementation of the ingestion controller. Currently this only serves to create new records in the
 * corresponding repository and read items from this repository. Update and delete operations are not yet implemented or
 * required.
 *
 * Currently the controller is using 2 maps to find out which repository to use for which type of ingestion item.
 *
 * @author Brendan Haesler
 */
public class BasicIngestionController extends AbstractSuroService implements IngestionController {


    /**
     * A mapping between time-based ingestion object classes and the identifier used to store the related repository in
     * the environment.
     */
    protected Map<Class<?>, String> repositoryMap;

    /**
     * The current mapping of time-based ingestion object to the list class for that type.
     */
    protected Map<Class<?>, Class<?>> itemToListMapping;

    /**
     * The currently used environment for this controller.
     */
    protected Environment environment;

    /*
     * Interface Methods
     */

	@Override
	@SuppressWarnings("unchecked")
	public <T, R extends TemporalList<T>> R createRecordFromList(long timestamp, List<T> records) {
		if (records == null || records.isEmpty()) {
			return null;
		}

		TemporalRepository<T, R> repository = (TemporalRepository<T, R>) getRepositoryByType(records.get(0).getClass());

		if (repository == null) {
			return null;
		}

		R ingestionList = ReflectionUtils.createInstance(itemToListMapping.get(records.get(0).getClass()));
		ingestionList.setTimestamp(timestamp);
		ingestionList.setRecords(records);
		repository.addItem(ingestionList);

		return ingestionList;
	}



	@Override
	public <T> List<T> getLatestRecordBeforeTime(Class<T> recordType, long time) {
		TemporalRepository<T, ?> ingestionRepository = getRepositoryByType(recordType);

		if (ingestionRepository == null) {
			return null;
		}

		TemporalList<T> record = ingestionRepository.findByTime(time);

		return record == null ? new ArrayList<T>() : record.getRecords();
	}


    /*
     * Service Level Methods
     */

	@Override
	protected void doBind(Environment environment) throws Exception {
        this.environment = environment;

        this.repositoryMap = new HashMap<Class<?>,String>();
		this.repositoryMap.put(BasePlanEntry.class, BasePlanListRepository.BASE_PLAN_REPOSITORY_INSTANCE);
		this.repositoryMap.put(WaitingPatient.class, WaitingPatientListRepository.WAITING_PATIENT_REPOSITORY_INSTANCE);
		this.repositoryMap.put(IcuAvailability.class, IcuAvailabilitiesRepository.ICU_AVAILABILITY_REPOSITORY_INSTANCE);
		this.repositoryMap.put(WardAvailability.class, WardAvailabilitiesRepository.WARD_AVAILABILITY_REPOSITORY_INSTANCE);
		this.repositoryMap.put(SpecialistAvailability.class, SpecialistAvailabilitiesRepository.SPECIALIST_AVAILABILITY_REPOSITORY_INSTANCE);

        this.itemToListMapping = new HashMap<Class<?>,Class<?>>();
        this.itemToListMapping.put(BasePlanEntry.class, BasePlanList.class);
        this.itemToListMapping.put(WaitingPatient.class, WaitingPatientList.class);
        this.itemToListMapping.put(IcuAvailability.class, IcuAvailabilityList.class);
        this.itemToListMapping.put(WardAvailability.class, WardAvailabilityList.class);
        this.itemToListMapping.put(SpecialistAvailability.class, SpecialistAvailabilityList.class);
	}

	@Override
	protected void doRelease() throws Exception {
        this.itemToListMapping = null;
		this.repositoryMap = null;
        this.environment = null;
	}


    /**
     * Retrieves the current environment used for this controller.
     * @return - the current environment
     */
    public Environment getEnvironment() {
        return this.environment;
    }

    /**
     * Retrieves the repository responsible to store the class/type of object passed.
     * @param type - the type of object you want to operate on
     * @param <T> - the type of the item (e.g. BasePlanEntry.class)
     * @param <R> - the type of the list (e.g. BasePlanList.class)
     * @return - a repository instance that is responsible to store objects of type T.
     */
    @SuppressWarnings("unchecked")
	protected <T, R extends TemporalList<T>> TemporalRepository<T, R> getRepositoryByType(Class<T> type) {
        if (type == null || repositoryMap.get(type) == null) {
            return null;
        }

        return (TemporalRepository<T, R>) environment.getAttribute(repositoryMap.get(type));
    }


}
