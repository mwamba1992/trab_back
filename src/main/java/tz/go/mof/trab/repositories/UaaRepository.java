package tz.go.mof.trab.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tz.go.mof.trab.models.OauthClientDetail;


@Repository
public interface UaaRepository extends JpaRepository<OauthClientDetail, String> {

	Optional<OauthClientDetail> findByClientId(String clientId);
}
