package org.isf.malnutrition.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.isf.malnutrition.dto.MalnutritionDTO;
import org.isf.malnutrition.manager.MalnutritionManager;
import org.isf.malnutrition.mapper.MalnutritionMapper;
import org.isf.malnutrition.model.Malnutrition;
import org.isf.medtype.rest.MedicalTypeController;
import org.isf.shared.exceptions.OHAPIException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "/malnutritions", produces = MediaType.APPLICATION_JSON_VALUE)
public class MalnutritionController {
	private final Logger logger = LoggerFactory.getLogger(MedicalTypeController.class);
	
	@Autowired
	private MalnutritionMapper mapper;
	
	@Autowired
	private MalnutritionManager manager;
	
	/**
	 * Stores a new {@link Malnutrition}. The malnutrition object is updated with the generated id.
	 * @param malnutrition the malnutrition to store.
	 * @return {@link ResponseEntity} with status <code>HttpStatus.CREATED</code> if the malnutrition has been stored
	 * @throws OHServiceException 
	 */
	@PostMapping(value = "/malnutritions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> newMalnutrition(@RequestBody @Valid MalnutritionDTO malnutritionDTO) throws OHServiceException{
		logger.info("Creating a new malnutrition ...");
		boolean isCreated = manager.newMalnutrition(mapper.map2Model(malnutritionDTO));
		if (!isCreated) {
			logger.info("Malnutrition is not created!");
            throw new OHAPIException(new OHExceptionMessage(null, "Malnutrition is not created!", OHSeverityLevel.ERROR));
        }
		logger.info("Malnutrition successfully created!");
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}
	
	/**
	 * Retrieves all the {@link Malnutrition} associated to the given admission id.
	 * @param admissionID the admission id to use as filter.
	 * @return all the retrieved malnutrition.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/malnutritions/{id_admission}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MalnutritionDTO>> getMalnutrition(@PathVariable("id_admission") String admissionID) throws OHServiceException{
		logger.info("Looking for malnutrition controls. Admission ID is " + admissionID);
		List<Malnutrition> malnutritions = manager.getMalnutrition(admissionID);
		if(malnutritions == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "Error while retrieving malnutrition controls!", OHSeverityLevel.ERROR));
		}
		List<MalnutritionDTO> mappedMalnutritions = mapper.map2DTOList(malnutritions);
		if(mappedMalnutritions.isEmpty()) {
			logger.info("No malnutrition control found");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mappedMalnutritions);
		} else {
			logger.info("Found " + mappedMalnutritions.size() + " malnutrition controls");
			return ResponseEntity.ok(mappedMalnutritions);
		}
	}
	
	/**
	 * returns the last {@link Malnutrition} entry for specified patient ID
	 * @param patientID - the patient ID
	 * @return the last {@link Malnutrition} for specified patient ID.
	 * @throws OHServiceException 
	 */
	@GetMapping(value = "/malnutritions/last/{id_patient}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MalnutritionDTO> getLastMalnutrition(@PathVariable("id_patient") int patientID) throws OHServiceException {
		Malnutrition foundMalnutrition = manager.getLastMalnutrition(patientID);
		if(foundMalnutrition == null) {
            throw new OHAPIException(new OHExceptionMessage(null, "No malnutrition found!", OHSeverityLevel.ERROR));
		} else {
			return ResponseEntity.ok(mapper.map2DTO(foundMalnutrition));
		}
	}
	
	/**
	 * Updates the specified malnutrition
	 * @param the {@link Malnutrition} to update
	 * @return the updated {@link Malnutrition}
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/malnutritions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MalnutritionDTO> updateMalnutrition(@RequestBody @Valid MalnutritionDTO malnutritionDTO) throws OHServiceException {
		Malnutrition updatedMalnutrition = manager.updateMalnutrition(mapper.map2Model(malnutritionDTO));
		return ResponseEntity.ok(mapper.map2DTO(updatedMalnutrition));
	}
	
	/**
	 * Deletes a given {@link Malnutrition}.
	 * @param malnutritionDTO - the malnutrition to delete.
	 * @return <code>true</code> if the malnutrition has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	@DeleteMapping(value = "/malnutritions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteMalnutrition(@RequestBody @Valid MalnutritionDTO malnutritionDTO) throws OHServiceException{
		List<Malnutrition> malnutritions = manager.getMalnutrition(malnutritionDTO.getAdmission().getId()+"");
		List<Malnutrition> matchedMalnutritions = new ArrayList<Malnutrition>();
		if(malnutritions != null) {
			matchedMalnutritions = malnutritions
					.stream()
					.filter(malnutrition -> malnutrition.getCode() == malnutritionDTO.getCode())
					.collect(Collectors.toList());
		}
		if(matchedMalnutritions.isEmpty()) {
			throw new OHAPIException(new OHExceptionMessage(null, "Malnutrition control not found!", OHSeverityLevel.ERROR));
		}
		boolean isDeleted = manager.deleteMalnutrition(matchedMalnutritions.get(0));
		return ResponseEntity.ok(isDeleted);
	}
}
