package io.github.arthoura.domain.repository;
import io.github.arthoura.domain.model.History;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HistoryRepository implements PanacheRepository<History> {
}
