package ru.vlsu.airline.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import ru.vlsu.airline.entities.Ticket;
import ru.vlsu.airline.repositories.JpaPersist;
import ru.vlsu.airline.repositories.TicketRepository;
import ru.vlsu.airline.statemachine.model.TicketEvent;
import ru.vlsu.airline.statemachine.model.TicketState;

@Configuration
@EnableStateMachine
public class StateMachineConfig extends StateMachineConfigurerAdapter<TicketState, TicketEvent> {


    @Override
    public void configure(StateMachineStateConfigurer<TicketState, TicketEvent> states) throws Exception {
        states
                .withStates()
                .initial(TicketState.UNPAID)
                .state(TicketState.PAID)
                .state(TicketState.UNABLED_TO_PAY)
                .end(TicketState.EXPIRED)
                .end(TicketState.CANCELLED)
                .end(TicketState.ANNULLED)
                .end(TicketState.USED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<TicketState, TicketEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(TicketState.UNPAID).target(TicketState.PAID).event(TicketEvent.PAY)
                .and()
                .withExternal()
                .source(TicketState.UNPAID).target(TicketState.EXPIRED).event(TicketEvent.EXPIRE)
                .and()
                .withExternal()
                .source(TicketState.PAID).target(TicketState.EXPIRED).event(TicketEvent.EXPIRE)
                .and()
                .withExternal()
                .source(TicketState.UNPAID).target(TicketState.UNABLED_TO_PAY).event(TicketEvent.UNABLE)
                .and()
                .withExternal()
                .source(TicketState.UNPAID).target(TicketState.CANCELLED).event(TicketEvent.CANCEL)
                .and()
                .withExternal()
                .source(TicketState.UNPAID).target(TicketState.ANNULLED).event(TicketEvent.ANNUL)
                .and()
                .withExternal()
                .source(TicketState.PAID).target(TicketState.ANNULLED).event(TicketEvent.ANNUL)
                .and()
                .withExternal()
                .source(TicketState.PAID).target(TicketState.USED).event(TicketEvent.USE);
    }

}
