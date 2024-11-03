package ch.unisg.omi.core.service;

import ch.unisg.omi.core.entity.Organization;
import ch.unisg.omi.core.port.in.SchemeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("Scheme")
public class SchemeService implements SchemeUseCase {

    private Organization organization = Organization.getOrganization();

    @Override
    public void startScheme(String schemeName) {

        try {
            organization.getOrgEntity().startScheme(schemeName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addScheme() {
        try {
            // TODO: Add scheme to the group represented by the group id
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
