package ru.vlsu.airline.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;
import ru.vlsu.airline.entities.Ticket;
import ru.vlsu.airline.entities.TicketStateEntity;
import ru.vlsu.airline.statemachine.model.TicketEvent;
import ru.vlsu.airline.statemachine.model.TicketState;

import java.util.Optional;

@Component
public class JpaPersist implements StateMachinePersist<TicketState, TicketEvent, Integer> {

    private final TicketStateRepository repository;

    @Autowired
    private TicketRepository ticketRepository;

    public JpaPersist(TicketStateRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(StateMachineContext<TicketState, TicketEvent> context, Integer contextObj) {
        TicketStateEntity entity = new TicketStateEntity();
        entity.setId(contextObj);

        TicketState stateToSet = (context.getState() != null) ? context.getState() : TicketState.UNPAID;

        entity.setState(stateToSet.name());

        Optional<Ticket> ticket = ticketRepository.findById(contextObj);
        if (ticket.isPresent()) {
            ticket.get().setStatus(stateToSet.name());
            ticketRepository.save(ticket.get());
        }

        repository.save(entity);
    }

    @Override
    public StateMachineContext<TicketState, TicketEvent> read(Integer contextObj) {
        TicketStateEntity entity = repository.findById(contextObj)
                .orElseThrow(() -> new RuntimeException("Состояние для объекта контекста не найдено: " + contextObj));

        TicketState state = TicketState.valueOf(entity.getState());

        return new DefaultStateMachineContext<>(state, null, null, null, null, "ticketStateMachine");
    }
}
