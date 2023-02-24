# COP4520-HW2-Problem2-KieranJimenez
## Problem 2: Minotaur’s Crystal Vase (50 points) 
The Minotaur decided to show his favorite crystal vase to his guests in a dedicated 
showroom with a single door. He did not want many guests to gather around the vase 
and accidentally break it. For this reason, he would allow only one guest at a time into 
the showroom. He asked his guests to choose from one of three possible strategies for 
viewing the Minotaur’s favorite crystal vase: 
1) Any guest could stop by and check whether the showroom’s door is open at any time 
and try to enter the room. While this would allow the guests to roam around the castle 
and enjoy the party, this strategy may also cause large crowds of eager guests to gather 
around the door. A particular guest wanting to see the vase would also have no 
guarantee that she or he will be able to do so and when. 
2) The Minotaur’s second strategy allowed the guests to place a sign on the door 
indicating when the showroom is available. The sign would read “AVAILABLE” or 
“BUSY.” Every guest is responsible to set the sign to “BUSY” when entering the 
showroom and back to “AVAILABLE” upon exit. That way guests would not bother trying 
to go to the showroom if it is not available. 
3) The third strategy would allow the quests to line in a queue. Every guest exiting the 
room was responsible to notify the guest standing in front of the queue that the 
showroom is available. Guests were allowed to queue multiple times. 

Which of these three strategies should the guests choose? Please discuss the advantages 
and disadvantages. 
Implement the strategy/protocol of your choice where each guest is represented by 1 
running thread. You can choose a concrete number for the number of guests or ask the 
user to specify it at the start. 

## Solution:
N set to 100, number of visits each guest can have set to 3
I think the guests should pick strategy 3, and I've chosen to implement it as an array-
based queue lock. Its disadvantages include the fact that it requires space for an array
to house all of the threads and the possibility of *false sharing*. Its advantages are 
that it is relatively fair and good at making sure each thread gets a turn. I also use
"Thread.onSpinWait()" so that the threads in the queue don't unnecessarily hog the
processor.
