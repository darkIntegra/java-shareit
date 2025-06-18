package ru.practicum.shareit.server.storage.request;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.model.request.Request;
import ru.practicum.shareit.server.repository.request.RequestRepository;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@Profile("!in-memory")
public class DatabaseRequestStorage implements RequestStorage {

    private final RequestRepository requestRepository;

    public DatabaseRequestStorage(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public Request addRequest(Request request) {
        return requestRepository.save(request);
    }

    @Override
    public Request updateRequest(Long requestId, Request updatedRequest) {
        return requestRepository.findById(requestId)
                .map(existingRequest -> {
                    existingRequest.setDescription(updatedRequest.getDescription());
                    existingRequest.setRequester(updatedRequest.getRequester());
                    return requestRepository.save(existingRequest);
                })
                .orElseThrow(() -> new NoSuchElementException("Запрос с ID=" + requestId + " не найден"));
    }

    @Override
    public Collection<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    @Override
    public Optional<Request> findRequestById(Long requestId) {
        return requestRepository.findById(requestId);
    }

    @Override
    public Collection<Request> getRequestsByRequestorId(Long requestorId) {
        return requestRepository.findByRequesterIdOrderByCreatedDesc(requestorId);
    }

    @Override
    public void deleteRequestById(Long requestId) {
        requestRepository.findById(requestId)
                .ifPresentOrElse(
                        request -> requestRepository.deleteById(requestId),
                        () -> {
                            throw new NoSuchElementException("Запрос с ID=" + requestId + " не найден");
                        }
                );
    }

    @Override
    public void deleteAllRequests() {
        requestRepository.deleteAll();
    }

    @Override
    public boolean existsById(Long requestId) {
        return requestRepository.existsById(requestId);
    }
}