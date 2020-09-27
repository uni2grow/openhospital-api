package org.isf.opetype.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.opetype.dto.OperationTypeDTO;
import org.isf.opetype.manager.OperationTypeBrowserManager;
import org.isf.opetype.mapper.OperationTypeMapper;
import org.isf.opetype.model.OperationType;
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
@Api(value = "/operationtypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class OperationTypeController {

	@Autowired
	protected OperationTypeBrowserManager opeTypeManager;
	
	@Autowired
	protected OperationTypeMapper mapper;

	private final Logger logger = LoggerFactory.getLogger(OperationTypeController.class);

	public OperationTypeController(OperationTypeBrowserManager opeTypeManager, OperationTypeMapper operationTypemapper) {
		this.opeTypeManager = opeTypeManager;
		this.mapper = operationTypemapper;
	}

	/**
	 * create a new {@link OperationType}
	 * @param operationTypeDTO
	 * @return <code>true</code> if the operation type has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PostMapping(value = "/operationtypes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> newOperationType(@RequestBody OperationTypeDTO operationTypeDTO) throws OHServiceException {
		String code = operationTypeDTO.getCode();
		logger.info("Create operation Type " + code);
		boolean isCreated = opeTypeManager.newOperationType(mapper.map2Model(operationTypeDTO));
		OperationType opeTypeCreated = opeTypeManager.getOperationType().stream().filter(opetype -> opetype.getCode().equals(code))
				.findFirst().orElse(null);
		if (!isCreated || opeTypeCreated == null) {
			throw new OHAPIException(new OHExceptionMessage(null, "operation Type is not created!", OHSeverityLevel.ERROR));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(opeTypeCreated.getCode());
	}

	/**
	 * Updates the specified {@link OperationType}.
	 * @param operationTypeDTO
	 * @return <code>true</code> if the operation type has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@PutMapping(value = "/operationtypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> updateOperationTypet(@PathVariable String code, @RequestBody OperationTypeDTO operationTypeDTO)
			throws OHServiceException {
		logger.info("Update operationtypes code:" + operationTypeDTO.getCode());
		OperationType opeType = mapper.map2Model(operationTypeDTO);
		if (!opeTypeManager.codeControl(code))
			throw new OHAPIException(new OHExceptionMessage(null, "operation Type not found!", OHSeverityLevel.ERROR));
		boolean isUpdated = opeTypeManager.updateOperationType(opeType);
		if (!isUpdated)
			throw new OHAPIException(new OHExceptionMessage(null, "operation Type is not updated!", OHSeverityLevel.ERROR));
		return ResponseEntity.ok(opeType.getCode());
	}

	/**
	 * get all the available {@link OperationType}s.
	 * @return a {@link List} of {@link OperationType} or NO_CONTENT if there is no data found.
	 * @throws OHServiceException
	 */
	@GetMapping(value = "/operationtypes", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OperationTypeDTO>> getOperationTypes() throws OHServiceException {
		logger.info("Get all operation Types ");
		List<OperationType> operationTypes = opeTypeManager.getOperationType();
		List<OperationTypeDTO> operationTypeDTOs = mapper.map2DTOList(operationTypes);
		if (operationTypeDTOs.size() == 0) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(operationTypeDTOs);
		} else {
			return ResponseEntity.ok(operationTypeDTOs);
		}
	}

	/**
	 * Delete {@link OperationType} for specified code.
	 * @param code
	 * @return <code>true</code> if the {@link OperationType} has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	@DeleteMapping(value = "/operationtypes/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> deleteOperationType(@PathVariable("code") String code) throws OHServiceException {
		logger.info("Delete operation Type code:" + code);
		boolean isDeleted = false;
		if (opeTypeManager.codeControl(code)) {
			List<OperationType> opeTypes = opeTypeManager.getOperationType();
			List<OperationType> opeTypeFounds = opeTypes.stream().filter(ad -> ad.getCode().equals(code))
					.collect(Collectors.toList());
			if (opeTypeFounds.size() > 0)
				isDeleted = opeTypeManager.deleteOperationType(opeTypeFounds.get(0));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return ResponseEntity.ok(isDeleted);
	}

}
