package tz.go.mof.trab.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.go.mof.trab.dto.migration.DuplicateGroup;
import tz.go.mof.trab.dto.migration.MigrationPhase1Result;
import tz.go.mof.trab.dto.migration.MigrationPhase2Result;
import tz.go.mof.trab.models.Appellant;
import tz.go.mof.trab.models.Appeals;
import tz.go.mof.trab.repositories.AppealantRepository;
import tz.go.mof.trab.repositories.AppealsRepository;

import java.util.*;

@Service
public class AppellantMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(AppellantMigrationService.class);

    @Autowired
    private AppealsRepository appealsRepository;

    @Autowired
    private AppealantRepository appealantRepository;

    /**
     * Normalize appellant name for grouping:
     * - Trim leading/trailing whitespace
     * - Uppercase
     * - Collapse multiple spaces to single space
     * - Remove dots (A.B.C -> ABC)
     * - Normalize common suffixes: LIMITED -> LTD, COMPANY -> CO
     */
    private String normalizeName(String name) {
        if (name == null) return "";
        String n = name.trim().toUpperCase();
        // Remove dots
        n = n.replace(".", "");
        // Collapse multiple spaces/tabs to single space
        n = n.replaceAll("\\s+", " ");
        // Normalize common suffixes
        n = n.replace("LIMITED", "LTD")
             .replace("COMPANY", "CO")
             .replace("CORPORATION", "CORP")
             .replace("INCORPORATED", "INC");
        return n.trim();
    }

    /**
     * PHASE 1: Extract unique appellants from appeals and insert into Appellant table.
     * No appeals are modified — only the Appellant table gets populated.
     */
    public MigrationPhase1Result extractAppellants(boolean dryRun) {
        MigrationPhase1Result result = new MigrationPhase1Result();

        // 1. Load ALL appeals
        List<Appeals> allAppeals = new ArrayList<>();
        appealsRepository.findAll().forEach(allAppeals::add);
        result.setTotalAppealsScanned(allAppeals.size());

        // 2. Normalize and group
        Map<String, List<Appeals>> groupedByNormalized = new LinkedHashMap<>();
        Map<String, Set<String>> originalVariantsByNormalized = new LinkedHashMap<>();

        for (Appeals a : allAppeals) {
            String name = a.getAppellantName();
            if (name == null || name.trim().isEmpty()) continue;

            String normalized = normalizeName(name);
            if (normalized.isEmpty()) continue;

            groupedByNormalized.computeIfAbsent(normalized, k -> new ArrayList<>()).add(a);
            originalVariantsByNormalized.computeIfAbsent(normalized, k -> new LinkedHashSet<>()).add(name.trim());
        }

        result.setUniqueNamesFound(groupedByNormalized.size());

        // 3. Build duplicate variants list (ALL groups shown for transparency)
        List<DuplicateGroup> duplicates = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : originalVariantsByNormalized.entrySet()) {
            DuplicateGroup dg = new DuplicateGroup();
            dg.setNormalizedName(entry.getKey());
            dg.setOriginalVariants(new ArrayList<>(entry.getValue()));
            dg.setAppealCount(groupedByNormalized.get(entry.getKey()).size());
            duplicates.add(dg);
        }
        result.setDuplicateVariants(duplicates);

        // 4. For each unique normalized name, check if Appellant exists, otherwise create
        int created = 0;
        int skipped = 0;

        for (Map.Entry<String, List<Appeals>> entry : groupedByNormalized.entrySet()) {
            String normalizedName = entry.getKey();
            List<Appeals> appeals = entry.getValue();

            // Pick richest data from any appeal in this group
            String bestTin = null, bestEmail = null, bestPhone = null, bestNatOfBus = null;
            String originalName = appeals.get(0).getAppellantName().trim();

            for (Appeals a : appeals) {
                if (bestTin == null && a.getTinNumber() != null && !a.getTinNumber().trim().isEmpty()
                        && !"NONE".equalsIgnoreCase(a.getTinNumber().trim())) {
                    bestTin = a.getTinNumber().trim();
                }
                if (bestEmail == null && a.getEmail() != null && !a.getEmail().trim().isEmpty()) {
                    bestEmail = a.getEmail().trim();
                }
                if (bestPhone == null && a.getPhone() != null && !a.getPhone().trim().isEmpty()) {
                    bestPhone = a.getPhone().trim();
                }
                if (bestNatOfBus == null && a.getNatOfBus() != null && !a.getNatOfBus().trim().isEmpty()) {
                    bestNatOfBus = a.getNatOfBus().trim();
                }
            }

            // Check if already exists by TIN
            boolean exists = false;
            if (bestTin != null) {
                Optional<Appellant> byTin = appealantRepository.findByTinNumber(bestTin);
                if (byTin.isPresent()) {
                    exists = true;
                }
            }

            // Check by name if not found by TIN — try multiple match strategies
            if (!exists) {
                // Try exact match on normalized name
                Appellant byName = appealantRepository.findByFirstNameIgnoreCase(normalizedName);
                if (byName != null) {
                    exists = true;
                }
                // Try original name
                if (!exists) {
                    byName = appealantRepository.findByFirstNameIgnoreCase(originalName);
                    if (byName != null) {
                        exists = true;
                    }
                }
                // Try checking all existing appellants with normalization
                if (!exists) {
                    List<Appellant> allAppellants = new ArrayList<>();
                    appealantRepository.findAll().forEach(allAppellants::add);
                    for (Appellant existing : allAppellants) {
                        if (existing.getFirstName() != null
                                && normalizeName(existing.getFirstName()).equals(normalizedName)) {
                            exists = true;
                            break;
                        }
                    }
                }
            }

            if (exists) {
                skipped++;
            } else {
                if (!dryRun) {
                    Appellant appellant = new Appellant();
                    appellant.setFirstName(originalName);
                    appellant.setLastName("");
                    appellant.setTinNumber(bestTin != null ? bestTin : "NONE");
                    appellant.setEmail(bestEmail != null ? bestEmail : "");
                    appellant.setPhoneNumber(bestPhone != null ? bestPhone : "");
                    appellant.setNatureOfBusiness(bestNatOfBus != null ? bestNatOfBus : "");
                    appellant.setCreatedDate(new Date());
                    appealantRepository.save(appellant);
                    logger.info("Created Appellant: {} (TIN: {})", originalName, bestTin);
                }
                created++;
            }
        }

        result.setAppellantsCreated(created);
        result.setAppellantsSkipped(skipped);

        logger.info("Phase 1 {} complete: scanned={}, unique={}, created={}, skipped={}, groups={}",
                dryRun ? "PREVIEW" : "EXECUTE",
                result.getTotalAppealsScanned(), result.getUniqueNamesFound(),
                result.getAppellantsCreated(), result.getAppellantsSkipped(),
                duplicates.size());

        return result;
    }

    /**
     * PHASE 2: Link appeals to Appellant records via FK.
     * Run AFTER Phase 1 is verified.
     */
    public MigrationPhase2Result linkAppeals(boolean dryRun) {
        MigrationPhase2Result result = new MigrationPhase2Result();

        List<Appeals> allAppeals = new ArrayList<>();
        appealsRepository.findAll().forEach(allAppeals::add);

        // Pre-load all appellants and build normalized lookup map
        List<Appellant> allAppellants = new ArrayList<>();
        appealantRepository.findAll().forEach(allAppellants::add);

        Map<String, Appellant> normalizedNameMap = new LinkedHashMap<>();
        Map<String, Appellant> tinMap = new LinkedHashMap<>();
        for (Appellant ap : allAppellants) {
            if (ap.getFirstName() != null) {
                normalizedNameMap.put(normalizeName(ap.getFirstName()), ap);
            }
            if (ap.getTinNumber() != null && !ap.getTinNumber().trim().isEmpty()
                    && !"NONE".equalsIgnoreCase(ap.getTinNumber().trim())) {
                tinMap.put(ap.getTinNumber().trim(), ap);
            }
        }

        int processed = 0, linked = 0, alreadyLinked = 0, unmatched = 0;
        List<String> unmatchedNos = new ArrayList<>();

        for (Appeals appeal : allAppeals) {
            processed++;

            if (appeal.getAppellant() != null) {
                alreadyLinked++;
                continue;
            }

            String name = appeal.getAppellantName();
            if (name == null || name.trim().isEmpty()) {
                unmatched++;
                unmatchedNos.add(appeal.getAppealNo() != null ? appeal.getAppealNo() : "ID:" + appeal.getAppealId());
                continue;
            }

            Appellant matched = null;

            // 1. By TIN
            String tin = appeal.getTinNumber();
            if (tin != null && !tin.trim().isEmpty() && !"NONE".equalsIgnoreCase(tin.trim())) {
                matched = tinMap.get(tin.trim());
            }

            // 2. By normalized name
            if (matched == null) {
                String normalized = normalizeName(name);
                matched = normalizedNameMap.get(normalized);
            }

            if (matched != null) {
                if (!dryRun) {
                    appeal.setAppellant(matched);
                    appealsRepository.save(appeal);
                }
                linked++;
            } else {
                unmatched++;
                unmatchedNos.add(appeal.getAppealNo() != null ? appeal.getAppealNo() : "ID:" + appeal.getAppealId());
            }
        }

        result.setTotalAppealsProcessed(processed);
        result.setAppealsLinked(linked);
        result.setAppealsAlreadyLinked(alreadyLinked);
        result.setAppealsUnmatched(unmatched);
        result.setUnmatchedAppealNos(unmatchedNos);

        logger.info("Phase 2 {} complete: processed={}, linked={}, alreadyLinked={}, unmatched={}",
                dryRun ? "PREVIEW" : "EXECUTE",
                processed, linked, alreadyLinked, unmatched);

        return result;
    }
}
