package it.gov.pagopa.payhub.auth.service;


import it.gov.pagopa.payhub.auth.mypay.repository.MyPayOperatorsRepository;
import it.gov.pagopa.payhub.auth.mypivot.repository.MyPivotOperatorsRepository;
import it.gov.pagopa.payhub.auth.repository.OperatorsRepository;
import it.gov.pagopa.payhub.auth.service.user.UserService;
import it.gov.pagopa.payhub.model.generated.OperatorDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuthzServiceImpl implements AuthzService {

    private final UserService userService;
    private final OperatorsRepository operatorsRepository;
    private final MyPayOperatorsRepository myPayOperatorsRepository;
    private final MyPivotOperatorsRepository myPivotOperatorsRepository;

    public AuthzServiceImpl(UserService userService, OperatorsRepository operatorsRepository, MyPayOperatorsRepository myPayOperatorsRepository,
        MyPivotOperatorsRepository myPivotOperatorsRepository) {
        this.userService = userService;
        this.operatorsRepository = operatorsRepository;
        this.myPayOperatorsRepository = myPayOperatorsRepository;
        this.myPivotOperatorsRepository = myPivotOperatorsRepository;
    }

    @Override
    public Page<OperatorDTO> getOrganizationOperators(String organizationIpaCode, Pageable pageRequest) {
        return userService.retrieveOrganizationOperators(organizationIpaCode, pageRequest);
    }

    @Override
    public void deleteOrganizationOperator(String organizationIpaCode, String operatorId) {
        myPayOperatorsRepository.deleteOrganizationOperator(operatorId, organizationIpaCode);
        myPivotOperatorsRepository.deleteOrganizationOperator(operatorId, organizationIpaCode);
        operatorsRepository.deleteByOperatorIdAndOrganizationIpaCode(operatorId, organizationIpaCode);
    }
}
