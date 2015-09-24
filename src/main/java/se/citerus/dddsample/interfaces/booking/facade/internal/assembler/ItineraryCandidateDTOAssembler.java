package se.citerus.dddsample.interfaces.booking.facade.internal.assembler;

import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.RouteCandidateDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Assembler class for the ItineraryCandidateDTO.
 */
public class ItineraryCandidateDTOAssembler {

    /**
     * @param itinerary itinerary
     * @return A route candidate DTO
     */
    public RouteCandidateDTO toDTO(final Itinerary itinerary) {
        final List<LegDTO> legDTOs = itinerary.legs()
                .stream()
                .map(this::toLegDTO)
                .collect(Collectors.toList());

        return new RouteCandidateDTO(legDTOs);
    }

    /**
     * @param leg leg
     * @return A leg DTO
     */
    protected LegDTO toLegDTO(final Leg leg) {
        final VoyageNumber voyageNumber = leg.voyage().voyageNumber();
        final UnLocode from = leg.loadLocation().unLocode();
        final UnLocode to = leg.unloadLocation().unLocode();
        return new LegDTO(voyageNumber.idString(), from.idString(), to.idString(), leg.loadTime(), leg.unloadTime());
    }

    /**
     * @param routeCandidateDTO  route candidate DTO
     * @param voyageRepository   voyage repository
     * @param locationRepository location repository
     * @return An itinerary
     */
    public Itinerary fromDTO(final RouteCandidateDTO routeCandidateDTO,
                             final VoyageRepository voyageRepository,
                             final LocationRepository locationRepository) {
        final List<Leg> legs = routeCandidateDTO.getLegs()
                .stream()
                .map(legDTO -> {
                    final VoyageNumber voyageNumber = new VoyageNumber(legDTO.getVoyageNumber());
                    final Voyage voyage = voyageRepository.find(voyageNumber);
                    final Location from = locationRepository.find(new UnLocode(legDTO.getFrom()));
                    final Location to = locationRepository.find(new UnLocode(legDTO.getTo()));
                    return new Leg(voyage, from, to, legDTO.getLoadTime(), legDTO.getUnloadTime());
                })
                .collect(Collectors.toList());

        return new Itinerary(legs);
    }
}