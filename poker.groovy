#!/usr/bin/env groovy

/* 5-card draw poker
   Royal Flush     250
   Straight Flush   50
   Four of a Kind   25
   Full House        9
   Flush             6
   Straight          4
   Three of a Kind   3
   Two Pair          2
   Jacks or Better   1 */

def init_game() {
    Integer running_total = 100
    System.out.print("\033[H\033[2J")
    System.out.flush()
    println 'You have been awarded 100 free chips!!! Good luck!' + '\7'
    System.console().readLine '(press any key to continue)'
    return running_total
}

def init_deck() {
    List deck = []
    List suits = ['H','C','D','S']
    List ranks = ['2','3','4','5','6','7','8','9','0','J','Q','K','A']
    List vals = [2,3,4,5,6,7,8,9,10,11,12,13,14]*4
    for (suit in suits) {
        for (rank in ranks) {
            deck.add([rank + suit,0])
        }
    for (i = 0; i < deck.size(); i++) {
        deck[i][1] = vals[i]            
        }
    }
    System.out.print("\033[H\033[2J")
    System.out.flush()
    return deck
}

def init_num_hands(running_total) {
    println 'Balance: ' + running_total.toString()
    try {
        num_hands
    } catch (MissingPropertyException ex) {
            if (running_total >= 100) {
                num_hands = 100
            }
            else {
                num_hands = running_total
            }
    }
    if (num_hands > running_total) {
        num_hands = running_total
    }

    Boolean input_err = true
    while (input_err) {
        String input = System.console().readLine "Enter number of hands [${num_hands.toString()}]: "
        if (input.trim() == '') {
            input_err = false
            input = num_hands
        }
        else {
            try {
                assert input.toInteger() > 0
                try {
                    assert input.toInteger() <= running_total
                    input_err = false
                    num_hands = input.toInteger()
                } catch (AssertionError e) {
                        println 'Number of hands cannot be greater than your chip balance.' + '\7'
                }
            } catch (AssertionError e) {
                    println 'Must be greater than zero.' + '\7'
            } catch (ValueError) {
                    println 'Must be a number greater than zero.' + '\7'
            }
        }
    }
    return num_hands
}

def deal(List deck) {
    List hand = []
    for (i = 0; i < 5; i++) {
        hand.add(deck.remove(0))
    }
    return hand
}

def hold_default(List hand) {
    List hold_hand = []
    List ranks = []
    List suits = []
    List high_cards = []
    String default_style = "${(char)27}[37;40m"
    for (card in hand) {
        ranks.add(card[0][0])
        suits.add(card[0][1])
        if (card[0][0] in ['J','Q','K','A']) {
            high_cards.add(card)
        }
    }
    Map grouped_ranks = ranks.countBy { it }
    Map grouped_suits = suits.countBy { it }
    //royal flush
    if (grouped_suits.size() == 1) {
        if (grouped_ranks == ['0':1,'J':1,'Q':1,'K':1,'A':1]) {
            println 'Royal flush!' + '\7'
            hold_hand = hand.collect()
        }
        //straight flush
        else if (grouped_ranks in [['A':1,'2':1,'3':1,'4':1,'5':1],['2':1,'3':1,'4':1,'5':1,'6':1],\
              ['3':1,'4':1,'5':1,'6':1,'7':1],['4':1,'5':1,'6':1,'7':1,'8':1],['5':1,'6':1,'7':1,'8':1,'9':1],\
              ['6':1,'7':1,'8':1,'9':1,'0':1],['7':1,'8':1,'9':1,'0':1,'J':1],['8':1,'9':1,'0':1,'J':1,'Q':1],\
              ['9':1,'0':1,'J':1,'Q':1,'K':1]]) {
            println 'straight flush!' + '\7'
            hold_hand = hand.collect()
        }
        //flush
        else {
            println 'Flush!' + '\7'
            hold_hand = hand.collect()
        }
    }
    else {
        if (grouped_ranks.size() == 2) {
            //four of a kind
            if (grouped_ranks.max{ it.value }.value == 4) {
                println 'Four of a kind!' + '\7'
                for (card in hand) {
                    if (card[0][0] == grouped_ranks.max{ it.value }.key) {
                        hold_hand.add(card)
                    }
                }
            }
            //full house
            else {
                println 'Full house!' + '\7'
                hold_hand = hand.collect()
            }
        }
        else if (grouped_ranks.size() == 3) {
            //three of a Kind
            if (grouped_ranks.max{ it.value }.value == 3) {
                println 'Three of a kind!' + '\7'
                for (card in hand) {
                    if (card[0][0] == grouped_ranks.max{ it.value }.key) {
                        hold_hand.add(card)
                    }
                }
            }
            //two pair
            else if (grouped_ranks.max{ it.value }.value == 2) {
                println 'Two pair!' + '\7'
                two_pair_ranks = grouped_ranks.findAll { it.value == 2 }
                for (card in hand) {
                    if (card[0][0] in two_pair_ranks) {
                        hold_hand.add(card)
                    }
                }
            }
        }
        //jacks or better
        else if (grouped_ranks.size() == 4 && grouped_ranks.max{ it.value }.value == 2 \
          && grouped_ranks.max{ it.value }.key in ['J','Q','K','A']) {
            println 'Jacks or better!' + '\7'
            for (card in hand) {
                if (card[0][0] == grouped_ranks.max{ it.value }.key) {
                    hold_hand.add(card)
                }
            }
        }
        //straight
        else if (grouped_ranks.size() == 5 && grouped_ranks in [['A':1,'2':1,'3':1,'4':1,'5':1],\
          ['2':1,'3':1,'4':1,'5':1,'6':1],['3':1,'4':1,'5':1,'6':1,'7':1],['4':1,'5':1,'6':1,'7':1,'8':1],\
          ['5':1,'6':1,'7':1,'8':1,'9':1],['6':1,'7':1,'8':1,'9':1,'0':1],['7':1,'8':1,'9':1,'0':1,'J':1],\
          ['8':1,'9':1,'0':1,'J':1,'Q':1],['9':1,'0':1,'J':1,'Q':1,'K':1],['0':1,'J':1,'Q':1,'K':1,'A':1]]) {
            println 'Straight!' + '\7'
            hold_hand = hand.collect()
        }
    }
    if (hold_hand == []) {
        //four of a flush
        if (grouped_suits.size() == 2 && grouped_suits.max{ it.value }.value == 4) {
            for (card in hand) {
                if (card[0][1] == grouped_suits.max{ it.value }.key) {
                    hold_hand.add(card)
                }
            }
        }
        //four of a straight
        else if (grouped_ranks.size() >= 4) {
            List temp_ranks = []
            for (card in hand) {
                if (card[1] in temp_ranks) {
                }
                else {
                    temp_ranks.add(card[1])
                }
            }
            if (temp_ranks.size() == 5) {
                if (temp_ranks.sort()[0..3] in [[2,3,4,14],[2,3,4,5],[3,4,5,6],[4,5,6,7],[5,6,7,8],\
                  [6,7,8,9],[7,8,9,10],[8,9,10,11],[9,10,11,12],[10,11,12,13],[11,12,13,14]]) {
                    for (card in hand) {
                        if (card[1] in temp_ranks.sort()[0..3]) {
                            hold_hand.add(card)
                        }
                    }
                }
                else if (temp_ranks.sort()[1..4] in [[2,3,4,14],[2,3,4,5],[3,4,5,6],[4,5,6,7],[5,6,7,8],\
                  [6,7,8,9],[7,8,9,10],[8,9,10,11],[9,10,11,12],[10,11,12,13],[11,12,13,14]]) {
                    for (card in hand) {
                        if (card[1] in temp_ranks.sort()[1..4]) {
                            hold_hand.add(card)
                        }
                    }
                }
            }
            else if (temp_ranks.size() == 4) {
                if (temp_ranks.sort() in [[2,3,4,14],[2,3,4,5],[3,4,5,6],\
                  [4,5,6,7],[5,6,7,8],[6,7,8,9],[7,8,9,10],[8,9,10,11],\
                  [9,10,11,12],[10,11,12,13],[11,12,13,14]]) {
                    for (rank in temp_ranks) {
                        card = hand.find { it[1] == rank }
                        hold_hand.add(card)
                    }
                }
            }
        }
    }
    if (hold_hand == []) {
        //tens or worse
        if (grouped_ranks.size() == 4 && grouped_ranks.max{ it.value }.value == 2 \
          && grouped_ranks.max{ it.value }.key in ['2','3','4','5','6','7','8','9','0']) {
            for (card in hand) {
                if (card[0][0] == grouped_ranks.max{ it.value }.key) {
                    hold_hand.add(card)
                }
            }
        }
        //two high cards
        else if (grouped_ranks.size() == 5 && high_cards.size() >= 2) {
            List high_card_ranks = []
            List high_card_suits = []
            for (card in high_cards) {
                high_card_ranks.add(card[0][0])
                high_card_suits.add(card[0][1])
            }
            Map grouped_high_card_suits = high_card_suits.countBy { it }
            if (high_cards.size() == 2) {
                if (grouped_high_card_suits.size() == 1) {
                    hold_hand = high_cards.collect()
                }
            }
            else {
                List temp_high_card_ranks = []
                for (card in high_cards) {
                    if (card[1] in temp_high_card_ranks) {
                    }
                    else {
                        temp_high_card_ranks.add(card[1])
                    }
                }
                if (grouped_high_card_suits.size() == 1) {
                    if (temp_high_card_ranks.sort()[0..1] in [[11,12],[12,13],[13,14]]) {
                        for (card in high_cards) {
                            if (card[1] in temp_high_card_ranks.sort()[0..1]) {
                                hold_hand.add(card)
                            }
                        }
                    }
                    else if (temp_high_card_ranks.sort()[1..2] in [[11,12],[12,13],[13,14]]) {
                        for (card in high_cards) {
                            if (card[1] in temp_high_card_ranks.sort()[1..2]) {
                                hold_hand.add(card)
                            }
                        }
                    }
                }
                else if (grouped_high_card_suits.size() == 2) {
                    for (i = 0; i < high_cards.size(); i++) {
                        if (high_cards[i][0][1] == grouped_high_card_suits.max{ it.value }.key) {
                            hold_hand.add(high_cards[i])
                        }
                    }
                }
                else if (grouped_high_card_suits.size() == 3) {
                    if (temp_high_card_ranks.sort()[0..1] in [[11,12],[12,13],[13,14]]) {
                        for (card in high_cards) {
                            if (card[1] in temp_high_card_ranks.sort()[0..1]) {
                                hold_hand.add(card)
                            }
                        }
                    }
                    else if (temp_high_card_ranks.sort()[1..2] in [[11,12],[12,13],[13,14]]) {
                        for (card in high_cards) {
                            if (card[1] in temp_high_card_ranks.sort()[1..2]) {
                                hold_hand.add(card)
                            }
                        }
                    }
                }
            }
        }
        //high card
        if (hold_hand == []) {
            List high_card = hand.max{ it[1] }
            if (high_card[0][0] in ['J','Q','K','A']) {
                hold_hand.add(high_card)
            }
        }
    }
    printf 'Suggested hold cards: [ '
        for (card in hold_hand) {
            style = colorize(card[0])
            printf style+card[0]
            printf default_style+' '
        }
        printf ']\n'
    return hold_hand
}

def show(List hand) {
    String default_style = "${(char)27}[37;40m"
    for (card in hand) {
        style = colorize(card[0])
        printf style+card[0]
        printf default_style+' '
    }
    printf '\n'
}

def discard(List hand, List hold_hand) {
    List replay_hand = hand.collect()
    Boolean input_err = true
    while(input_err) {
        String use_hold_hand = System.console().readLine 'Press Enter to use suggested holds or enter D to select discards. '
        if (use_hold_hand.trim() == '') {
            input_err = false
            hand = hold_hand.collect()
        }
        else if (use_hold_hand.toUpperCase() == 'D') {
            input_err = false
            List discard_hand = hand.collect()
            println 'Enter up to 5 cards to discard. You can use < ! > with ranks, = ! with suits.'
            Boolean discarding = true
            Integer discard_count = 0
            String default_style = "${(char)27}[37;40m"
            while (discarding && discard_count < 5) {
                Boolean discard_input_err = true
                while (discard_input_err) {
                    String discard = System.console().readLine 'Discard [none when finished]: '
                    if (discard.trim() == '') {
                        discard_input_err = false
                        discarding = false
                    }
                    else if (discard.trim().size() == 2) {
                        if (hand.findAll { it[0] == discard.trim().toUpperCase() } != []) {
                            discard_input_err = false
                            discard_hand.remove(hand.findAll { it[0] == discard.trim().toUpperCase() }[0])
                            discard_count += 1
                            show(discard_hand)
                            hand = discard_hand.collect()
                        }
                        else if (discard[0] == '<' && discard_hand.findAll {
                          it[1] < rank_value(discard[1].toUpperCase()) } != []) {
                            discard_input_err = false
                            for (card in discard_hand.findAll { it[1] < rank_value(discard[1].toUpperCase()) }) {
                                discard_hand.remove(card)
                                discard_count += 1
                            }
                            show(discard_hand)
                            hand = discard_hand.collect()
                        }
                        else if (discard[0] == '>' && discard_hand.findAll {
                          it[1] > rank_value(discard[1].toUpperCase()) } != []) {
                            discard_input_err = false
                            for (card in discard_hand.findAll { it[1] > rank_value(discard[1].toUpperCase()) }) {
                                discard_hand.remove(card)
                                discard_count += 1
                            }
                            show(discard_hand)
                            hand = discard_hand.collect()
                        }
                        else if (discard[0] == '!') {
                            try {
                                assert discard_hand.findAll { it[1] != rank_value(discard[1].toUpperCase()) } != []
                                discard_input_err = false
                                for (card in discard_hand.findAll { it[1] != rank_value(discard[1].toUpperCase()) }) {
                                    discard_hand.remove(card)
                                    discard_count += 1
                                }
                                show(discard_hand)
                                hand = discard_hand.collect()
                            } catch (NumberFormatException) {
                                assert discard_hand.findAll { it[0][1] != discard[1].toUpperCase() } != []
                                discard_input_err = false
                                for (card in discard_hand.findAll { it[0][1] != discard[1].toUpperCase() }) {
                                    discard_hand.remove(card)
                                    discard_count += 1
                                }
                                show(discard_hand)
                                hand = discard_hand.collect()
                            }
                        }
                        else if (discard[0] == '=' && \
                          discard_hand.findAll { it[0][1] == discard[1].toUpperCase() } != []) {
                            discard_input_err = false
                            for (card in discard_hand.findAll { it[0][1] == discard[1].toUpperCase() }) {
                                discard_hand.remove(card)
                                discard_count += 1
                            }
                            show(discard_hand)
                            hand = discard_hand.collect()
                        }
                    }
                    else {
                        printf 'Select a card from [ '
                        for (card in hand) {
                            style = colorize(card[0])
                            printf style+card[0]
                            printf default_style+' '
                        }
                        printf '].\n' + '\7'
                    }
                }
            }
        }
        else {
            print 'Press Enter to use suggested holds or D to select discards. ' + '\7'
        }
    }
    return [hand, replay_hand]
}

def draw(List hand, List deck, Integer num_hands) {
    List results = [[],0,'']
    for (i = 0; i < num_hands; i++) {
        results[i] = [[],0,'']
        hand.each {
            results[i][0].add(it)
        }
    }
    List working_deck = deck.collect()
    for (i = 0; i < num_hands; i++) {
        Integer z = 5-(results[i][0].size())
        for (j = 0; j < z; j++) {
            results[i][0].add(working_deck.remove(0))
        }
        paylist = pays(results[i][0])
        results[i][1] = paylist[1]
        results[i][2] = paylist[0]
        working_deck = deck.collect()
        Collections.shuffle(working_deck, new Random(System.nanoTime()))
    }
    return results
}

def show_all(List results, Integer num_hands, Integer running_total) {
    Integer hands_per_row = 6
    Integer hands_count = 0
    List scores = []
    Integer payout = -1 * num_hands
    String default_style = "${(char)27}[37;40m"
    for (i = 0; i < num_hands; i++) {
        for (j = 0; j < results[i][0].size(); j++) {
            style = colorize(results[i][0][j][0])
            printf style+results[i][0][j][0]
            printf default_style+' '
        }
        if (results[i][1] > 0) {
            payout += results[i][1] + 1
        }
        scores.add(results[i][2])
        hands_count += 1
        String z = results[i][1].toString()
        printf z
        for (k = 0; k < 5-z.size(); k++) {
            printf ' '
        }
        if (hands_count == hands_per_row) {
            hands_count = 0
            printf '\n'
        }
    }
    if (hands_count != hands_per_row) {
        printf '\n'
    }
    Map grouped_scores = scores.countBy { it }
    println grouped_scores.sort()
    if ( payout >= num_hands) {
        println 'WINNER!!!' + '\7'
    }
    println 'Hand pay: ' + payout.toString()
    running_total += payout
    println 'Balance: ' + running_total.toString()
    return [payout, running_total]
}

def pays(List hand) {
    List ranks = []
    List suits = []
    for (card in hand) {
        ranks.add(card[0][0])
        suits.add(card[0][1])
    }
    Map grouped_ranks = ranks.countBy { it }
    Map grouped_suits = suits.countBy { it }
    if (grouped_suits.size() == 1) {
        //Royal Flush
        if (grouped_ranks == ['0':1,'J':1,'Q':1,'K':1,'A':1]) {
            score = "royal flush"
            pay = 250
        }
        //Straight Flush
        else if (grouped_ranks in [['A':1,'2':1,'3':1,'4':1,'5':1],['2':1,'3':1,'4':1,'5':1,'6':1],\
              ['3':1,'4':1,'5':1,'6':1,'7':1],['4':1,'5':1,'6':1,'7':1,'8':1],['5':1,'6':1,'7':1,'8':1,'9':1],\
              ['6':1,'7':1,'8':1,'9':1,'0':1],['7':1,'8':1,'9':1,'0':1,'J':1],['8':1,'9':1,'0':1,'J':1,'Q':1],\
              ['9':1,'0':1,'J':1,'Q':1,'K':1]]) {
            score = "straight flush"
            pay = 50
        }
        //Flush
        else {
            score = "flush"
            pay = 6
        }
    }
    else {
        if (grouped_ranks.size() == 2) {
            //Four of a Kind
            if (grouped_ranks.max{ it.value }.value == 4) {
                score = "four of a kind"
                pay = 25
            }
            //Full House
            else {
                score = "full house"
                pay = 9
            }
        }
        else if (grouped_ranks.size() == 3) {
            //Three of a Kind
            if (grouped_ranks.max{ it.value }.value == 3) {
                score = "three of a kind"
                pay = 3
            }
            //Two Pair
            else if (grouped_ranks.max{ it.value }.value == 2) {
                score = "two pair"
                pay = 2
            }
        }
        else if (grouped_ranks.size() == 4) {
            //Jacks or Better
            if (grouped_ranks.max{ it.value }.value == 2 && grouped_ranks.max{ it.value }.key in ['J','Q','K','A']) {
                score = "jacks or better"
                pay = 1
            }
            else {
                score = "loser"
                pay  = 0
            }
        }
        else if (grouped_ranks.size() == 5) {
            //Straight
            if (grouped_ranks in [['A':1,'2':1,'3':1,'4':1,'5':1],['2':1,'3':1,'4':1,'5':1,'6':1],\
              ['3':1,'4':1,'5':1,'6':1,'7':1],['4':1,'5':1,'6':1,'7':1,'8':1],['5':1,'6':1,'7':1,'8':1,'9':1],\
              ['6':1,'7':1,'8':1,'9':1,'0':1],['7':1,'8':1,'9':1,'0':1,'J':1],['8':1,'9':1,'0':1,'J':1,'Q':1],\
              ['9':1,'0':1,'J':1,'Q':1,'K':1],['0':1,'J':1,'Q':1,'K':1,'A':1]]) {
                score = "straight"
                pay = 4
            }
            else {
                score = "loser"
                pay = 0
            }
        }
    }
    List paylist = [score, pay]
    return paylist
}

def end_of_game(play_again, replay_hand, payout, running_total) {
    println 'Game Over.'
    Boolean again_input_err = true
    while (again_input_err) {
        String again_yn = System.console().readLine 'Press <Enter> to play again or enter R to replay the hand or Q to quit: '
        if (again_yn.trim() == '') {
            again_input_err = false
            replay = false
            System.out.print("\033[H\033[2J")
            System.out.flush()
        }
        else if (again_yn.toUpperCase() == 'R') {
            again_input_err = false
            hand = replay_hand.collect()
            replay = true
            play_again = true
            running_total -= payout
            System.out.print("\033[H\033[2J")
            System.out.flush()
        }
        else if (again_yn.toUpperCase() == 'Q') {
            again_input_err = false
            replay = false
            play_again = false
            if (running_total > 0) {
                println 'Credit balance. Cash dispensed below.'
            }
            else if (running_total == 0) {
                println 'You leave with nothing. Play again if you dare!'
            }
            else {
                println 'Balance due. Insert credit card below.'
            }
        }
        else {
            println 'Try again. ' + '\7'
        }
    }
    return [replay, play_again, hand, running_total]
}

def rank_value(String card) {
    Integer value = 0
    if (card[0] == '0') { value = 10 }
    else if (card[0] == 'J') { value = 11 }
    else if (card[0] == 'Q') { value = 12 }
    else if (card[0] == 'K') { value = 13 }
    else if (card[0] == 'A') { value = 14 }
    else { value = card[0].toInteger() }
    return value
}

def colorize(String card) {
    if (card[1] == 'C') { style = "${(char)27}[34;40"+'m' }
    else if (card[1] == 'D') { style = "${(char)27}[91;40"+'m' }
    else if (card[1] == 'H') { style = "${(char)27}[31;40"+'m' }
    else if (card[1] == 'S') { style = "${(char)27}[94;40"+'m' }
    return style
}

def mainMethod() {
    Boolean play_again = true
    running_total = init_game()

    while (play_again) {

        //game loop
        deck = init_deck()
        num_hands = init_num_hands(running_total)
        Collections.shuffle(deck)
        hand = deal(deck)
        Boolean replay = true

        while (replay) {
            show(hand)
            hold_hand = hold_default(hand)
            (hand, replay_hand) = discard(hand, hold_hand)
            results = draw(hand, deck, num_hands)
            (payout, running_total) = show_all(results, num_hands, running_total)
            (replay, play_again, hand, running_total) = end_of_game(play_again, replay_hand, payout, running_total)
        }
    }
}

mainMethod()
