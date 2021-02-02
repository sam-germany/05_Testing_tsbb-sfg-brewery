/*
 *  Copyright 2019 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package guru.springframework.brewery.events;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class BeerOrderStatusChangeEventListener {

    @Async
    @EventListener
    public void listen(BeerOrderStatusChangeEvent event){
        System.out.println("I got an order status change event");
        System.out.println(event);
    }
}
/*
@Async                        https://www.baeldung.com/spring-async
-------

Note: to use @Async we need to put  @EnableAsync in the Application

@Async has two limitations:
(1)it must be applied to public methods only
(2)self-invocation – calling the async method from within the same class – won't work

(3) on the method which we put @Async   will be executed in a extra new Thread


 */
