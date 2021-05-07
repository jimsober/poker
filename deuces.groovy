#!/usr/bin/env groovy

/* 5-card draw deuces wild poker
   Natural Royal Flush 800
   Four Deuces         200
   Wild Royal Flush     25
   Five of a Kind       15
   Straight Flush        9
   Four of a Kind        5
   Full House            3
   Flush                 2
   Straight              2
   Three of a Kind       1 */

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
    List wild_cards = []
    String default_style = "${(char)27}[37;40m"
    for (card in hand) {
        if (card[0][0] == '2') {
            wild_cards.add(card)
        }
    }
    if (wild_cards.size() == 4) {
        println 'Four deuces!' + '\7'
        hold_hand = wild_cards.collect()
    }
    else if (wild_cards.size() == 3) {
        hold_hand = three_deuces(hand)
    }
    else if (wild_cards.size() == 2) {
        hold_hand = two_deuces(hand)
    }
    else if (wild_cards.size() == 1) {
        hold_hand = one_deuce(hand)
    }
    else {
        hold_hand = no_deuces(hand)
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

def three_deuces(List hand) {
    List hold_hand = []
    List ranks = []
    List suits = []
    List deuceless_ranks = []
    List deuceless_suits = []
    List high_cards = []
    List wild_cards = []
    List max_deuceless_suit_vals = []
    List deuceless_rank_vals = []
    for (card in hand) {
        ranks.add(card[0][0])
        suits.add(card[0][1])
        if (card[0][0] in ['J','Q','K','A']) {
            high_cards.add(card)
        }
        if (card[0][0] == '2') {
            wild_cards.add(card)
        }
        else {
            deuceless_ranks.add(card[0][0])
            deuceless_suits.add(card[0][1])
        }
        if (card[1] in deuceless_rank_vals || card[1] == 2) {
        }
        else {
            deuceless_rank_vals.add(card[1])
        }
    }
    Map grouped_ranks = ranks.countBy { it }
    Map grouped_suits = suits.countBy { it }
    Map grouped_deuceless_ranks = deuceless_ranks.countBy { it }
    Map grouped_deuceless_suits = deuceless_suits.countBy { it }
    for (card in hand) {
        if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key) {
            if (card[1] in max_deuceless_suit_vals || card[1] == 2) {
            }
            else {
                max_deuceless_suit_vals.add(card[1])
            }
        }
    }
    if (wild_cards.size() == 3) {
        //wild royal flush
        if (grouped_deuceless_suits.size() == 1 && grouped_deuceless_ranks in [['0':1,'J':1],['0':1,'Q':1],\
          ['0':1,'K':1],['0':1,'A':1],['J':1,'Q':1],['J':1,'K':1],['J':1,'A':1],['Q':1,'K':1],['Q':1,'A':1],\
          ['K':1,'A':1]]) {
            println 'Wild royal flush!' + '\7'
            hold_hand = hand.collect()
        }
        //five of a kind
        else if (grouped_deuceless_ranks.size() == 1) {
            println 'Five of a kind!' + '\7'
            hold_hand = hand.collect()
        }
        //three deuces
        else {
            println 'Four of a kind or better!' + '\7'
            hold_hand = wild_cards.collect()
        }
    }
    return hold_hand
}

def two_deuces(List hand) {
    List hold_hand = []
    List ranks = []
    List suits = []
    List deuceless_ranks = []
    List deuceless_suits = []
    List high_cards = []
    List wild_cards = []
    List max_deuceless_suit_vals = []
    List deuceless_rank_vals = []
    for (card in hand) {
        ranks.add(card[0][0])
        suits.add(card[0][1])
        if (card[0][0] in ['J','Q','K','A']) {
            high_cards.add(card)
        }
        if (card[0][0] == '2') {
            wild_cards.add(card)
        }
        else {
            deuceless_ranks.add(card[0][0])
            deuceless_suits.add(card[0][1])
        }
        if (card[1] in deuceless_rank_vals || card[1] == 2) {
        }
        else {
            deuceless_rank_vals.add(card[1])
        }
    }
    Map grouped_ranks = ranks.countBy { it }
    Map grouped_suits = suits.countBy { it }
    Map grouped_deuceless_ranks = deuceless_ranks.countBy { it }
    Map grouped_deuceless_suits = deuceless_suits.countBy { it }
    for (card in hand) {
        if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key) {
            if (card[1] in max_deuceless_suit_vals || card[1] == 2) {
            }
            else {
                max_deuceless_suit_vals.add(card[1])
            }
        }
    }
    if (wild_cards.size() == 2) {
        //wild royal flush
        if (grouped_deuceless_suits.size() == 1 && grouped_deuceless_ranks in [['0':1,'J':1,'Q':1],\
          ['0':1,'J':1,'K':1],['0':1,'J':1,'A':1],['0':1,'Q':1,'K':1],['0':1,'Q':1,'Q':1],['0':1,'K':1,'A':1],\
          ['J':1,'Q':1,'K':1],['J':1,'Q':1,'A':1],['J':1,'K':1,'A':1],['Q':1,'K':1,'A':1]]) {
            println 'Wild royal flush!' + '\7'
            hold_hand = hand.collect()
        }
        //five of a kind
        else if (grouped_deuceless_ranks.size() == 1) {
            println 'Five of a kind!' + '\7'
            hold_hand = hand.collect()
        }
        //straight flush
        else if (grouped_deuceless_suits.size() == 1 && grouped_deuceless_ranks in [['A':1,'3':1,'4':1],\
          ['A':1,'3':1,'5':1],['A':1,'4':1,'5':1],['3':1,'4':1,'5':1],['3':1,'4':1,'6':1],['3':1,'4':1,'7':1],\
          ['3':1,'5':1,'6':1],['3':1,'5':1,'7':1],['3':1,'6':1,'7':1],['4':1,'5':1,'6':1],['4':1,'5':1,'7':1],\
          ['4':1,'5':1,'8':1],['4':1,'6':1,'7':1],['4':1,'6':1,'8':1],['4':1,'7':1,'8':1],['5':1,'6':1,'7':1],\
          ['5':1,'6':1,'8':1],['5':1,'6':1,'9':1],['5':1,'7':1,'8':1],['5':1,'7':1,'9':1],['5':1,'8':1,'9':1],\
          ['6':1,'7':1,'8':1],['6':1,'7':1,'9':1],['6':1,'7':1,'0':1],['6':1,'8':1,'9':1],['6':1,'8':1,'0':1],\
          ['6':1,'9':1,'0':1],['7':1,'8':1,'9':1],['7':1,'8':1,'0':1],['7':1,'8':1,'J':1],['7':1,'9':1,'0':1],\
          ['7':1,'9':1,'J':1],['7':1,'0':1,'J':1],['8':1,'9':1,'0':1],['8':1,'9':1,'J':1],['8':1,'9':1,'Q':1],\
          ['8':1,'0':1,'J':1],['8':1,'0':1,'Q':1],['8':1,'J':1,'Q':1],['9':1,'0':1,'J':1],['9':1,'0':1,'Q':1],\
          ['9':1,'0':1,'K':1],['9':1,'J':1,'Q':1],['9':1,'J':1,'K':1],['9':1,'Q':1,'K':1],['0':1,'J':1,'Q':1],\
          ['0':1,'J':1,'K':1],['0':1,'J':1,'A':1],['0':1,'Q':1,'K':1],['0':1,'Q':1,'A':1],['0':1,'K':1,'A':1],\
          ['J':1,'Q':1,'K':1],['J':1,'Q':1,'A':1],['J':1,'K':1,'A':1]]) {
            println 'Straight Flush!' + '\7'
            hold_hand = hand.collect()
        }
        //four of a kind
        else if (grouped_deuceless_ranks.max{ it.value }.value == 2) {
            println 'Four of a kind!' + '\7'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][0] == grouped_deuceless_ranks.max{ it.value }.key) {
                    hold_hand.add(card)
                }
            }
        }
        //four of a royal flush
        else if (grouped_deuceless_suits.max{ it.value }.value == 2 && max_deuceless_suit_vals.sort() in \
          [[10,11],[10,12],[10,13],[10,14],[11,12],[11,13],[11,14],[12,13],[12,14],[13,14]]) {
            println 'Three of a kind or better (four of a royal flush)!' + '\7'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[1] in max_deuceless_suit_vals) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 3 && max_deuceless_suit_vals.sort()[1..2] in \
          [[10,11],[10,12],[10,13],[10,14],[11,12],[11,13],[11,14],[12,13],[12,14],[13,14]]) {
            println 'Three of a kind or better (four of a royal flush)!' + '\7'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[1] in max_deuceless_suit_vals.sort()[1..2]) {
                    hold_hand.add(card)
                }
            }
        }
        //four of a straight flush
        else if (grouped_deuceless_suits.max{ it.value }.value == 2  && max_deuceless_suit_vals.sort() in [[3,14],\
          [4,14],[3,4],[3,5],[3,6],[3,7],[4,5],[4,6],[4,7],[4,8],[5,6],[5,7],[5,8],[5,9],[6,7],[6,8],[6,9],[6,10],\
          [7,8],[7,9],[7,10],[7,11],[8,9],[8,10],[8,11],[8,12],[9,10],[9,11],[9,12],[9,13],[10,11],[10,12],[10,13],\
          [10,14],[11,12],[11,13],[11,14],[12,13],[12,14],[13,14]]) {
            println 'Three of a kind or better (four of a straight flush)!' + '\7'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[1] in max_deuceless_suit_vals.sort() && card[0][1] == grouped_deuceless_suits.max{ it.value}.key) {
                    hold_hand.add(card)
                }
            }
        }
        //two deuces
        else {
            println 'Three of a kind or better!' + '\7'
            hold_hand = wild_cards.collect()
        }
    }
    return hold_hand
}

def one_deuce(List hand) {
    List hold_hand = []
    List ranks = []
    List suits = []
    List deuceless_ranks = []
    List deuceless_suits = []
    List high_cards = []
    List wild_cards = []
    List max_deuceless_suit_vals = []
    List deuceless_rank_vals = []
    List max_suits = []
    for (card in hand) {
        ranks.add(card[0][0])
        suits.add(card[0][1])
        if (card[0][0] in ['J','Q','K','A']) {
            high_cards.add(card)
        }
        if (card[0][0] == '2') {
            wild_cards.add(card)
        }
        else {
            deuceless_ranks.add(card[0][0])
            deuceless_suits.add(card[0][1])
        }
        if (card[1] in deuceless_rank_vals || card[1] == 2) {
        }
        else {
            deuceless_rank_vals.add(card[1])
        }
    }
    Map grouped_ranks = ranks.countBy { it }
    Map grouped_suits = suits.countBy { it }
    Map grouped_deuceless_ranks = deuceless_ranks.countBy { it }
    Map grouped_deuceless_suits = deuceless_suits.countBy { it }
    for (card in hand) {
        if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key) {
            if (card[1] in max_deuceless_suit_vals || card[1] == 2) {
            }
            else {
                max_deuceless_suit_vals.add(card[1])
            }
        }
    }
    for (val in grouped_deuceless_suits) {
      if (val.value == grouped_deuceless_suits.max{ it.value }.value) {
        max_suits.add(val.key)
      }
    }
    if (max_suits.size() > 1) {
        max_suits2 = max_suits.collect()
        max_suits2.remove(grouped_deuceless_suits.max{ it.value }.key)
        max_deuceless_suit_vals2 = []
        for (card in hand) {
            if (card[0][1] == max_suits2[0]) {
                if (card[1] in max_deuceless_suit_vals2 || card[1] == 2) {
                }
                else {
                    max_deuceless_suit_vals2.add(card[1])
                }
            }
        }
    }
    if (wild_cards.size() == 1) {
        //wild royal flush
        if (grouped_deuceless_suits.size() == 1 && grouped_deuceless_ranks in [['0':1,'J':1,'Q':1,'A':1],\
          ['0':1,'J':1,'Q':1,'K':1],['0':1,'J':1,'K':1,'A':1],['0':1,'Q':1,'K':1,'A':1],\
          ['J':1,'Q':1,'K':1,'A':1]]) {
            println 'Wild royal flush!' + '\7'
            hold_hand = hand.collect()
        }
        //five of a kind
        else if (grouped_deuceless_ranks.size() == 1) {
            println 'Five of a kind!' + '\7'
            hold_hand = hand.collect()
        }
        //straight flush
        else if (grouped_deuceless_suits.size() == 1 && grouped_deuceless_ranks in [['A':1,'3':1,'4':1,'5':1],\
          ['3':1,'4':1,'5':1,'6':1],['3':1,'5':1,'6':1,'7':1],['3':1,'4':1,'6':1,'7':1],['3':1,'4':1,'5':1,'7':1],\
          ['4':1,'5':1,'6':1,'7':1],['4':1,'6':1,'7':1,'8':1],['4':1,'5':1,'7':1,'8':1],['4':1,'5':1,'6':1,'8':1],\
          ['5':1,'6':1,'7':1,'8':1],['5':1,'7':1,'8':1,'9':1],['5':1,'6':1,'8':1,'9':1],['5':1,'6':1,'7':1,'9':1],\
          ['6':1,'7':1,'8':1,'9':1],['6':1,'7':1,'8':1,'0':1],['7':1,'8':1,'9':1,'0':1],['6':1,'8':1,'9':1,'0':1],\
          ['6':1,'7':1,'9':1,'0':1],['7':1,'9':1,'0':1,'J':1],['7':1,'8':1,'0':1,'J':1],['8':1,'9':1,'0':1,'J':1],\
          ['7':1,'8':1,'9':1,'J':1],['8':1,'9':1,'0':1,'Q':1],['8':1,'9':1,'J':1,'Q':1],['8':1,'0':1,'J':1,'Q':1],\
          ['9':1,'0':1,'J':1,'Q':1],['0':1,'J':1,'Q':1,'K':1],['9':1,'0':1,'Q':1,'K':1],['9':1,'J':1,'Q':1,'K':1],\
          ['9':1,'0':1,'J':1,'K':1]]) {
            println 'Straight Flush!' + '\7'
            hold_hand = hand.collect()
        }
        //four of a kind
        else if (grouped_deuceless_ranks.max{ it.value }.value == 3) {
            println 'Four of a kind!' + '\7'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][0] == grouped_deuceless_ranks.max{ it.value }.key) {
                    hold_hand.add(card)
                }
            }
        }
        //four of a royal flush
        else if (grouped_deuceless_suits.max{ it.value }.value == 3 && max_deuceless_suit_vals.sort() in [[10,11,12],\
          [10,11,13],[10,11,14],[10,12,13],[10,12,14],[10,13,14],[11,12,13],[11,12,14],[11,13,14],[12,13,14]]) {
            println 'Four of a royal flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[1] in max_deuceless_suit_vals && card[0][1] == grouped_deuceless_suits.max{ it.value }.key) {
                    hold_hand.add(card)
                }
            }
        }
        //full house
        else if (grouped_deuceless_ranks.size() == 2) {
            println 'Full house!' + '\7'
            hold_hand = hand.collect()
        }
        //four card straight flush
        else if (grouped_deuceless_suits.max{ it.value }.value == 3 && max_deuceless_suit_vals.sort() in \
          [[3,4,14],[3,4,5],[3,4,6],[3,5,6],[4,5,6],[4,5,7],[4,6,7],[5,6,7],[5,6,8],[5,7,8],[6,7,8],[6,7,9],\
          [6,8,9],[7,8,9],[7,8,10],[7,9,10],[8,9,10],[8,9,11],[8,10,11],[9,10,11],[9,10,12],[9,11,12],\
          [10,11,12],[10,11,13],[10,12,13],[11,12,13],[11,12,14],[11,13,14],[12,13,14]]) {
            println 'Four card straight flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 4 && max_deuceless_suit_vals.sort()[0..2] in \
          [[3,4,14],[3,4,5],[3,4,6],[3,5,6],[4,5,6],[4,5,7],[4,6,7],[5,6,7],[5,6,8],[5,7,8],[6,7,8],[6,7,9],\
          [6,8,9],[7,8,9],[7,8,10],[7,9,10],[8,9,10],[8,9,11],[8,10,11],[9,10,11],[9,10,12],[9,11,12],\
          [10,11,12],[10,11,13],[10,12,13],[11,12,13],[11,12,14],[11,13,14],[12,13,14]]) {
            println 'Four card straight flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key && \
                  card[1] in max_deuceless_suit_vals.sort()[0..2]) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 4 && max_deuceless_suit_vals.sort()[1..3] in \
          [[3,4,14],[3,4,5],[3,4,6],[3,5,6],[4,5,6],[4,5,7],[4,6,7],[5,6,7],[5,6,8],[5,7,8],[6,7,8],[6,7,9],\
          [6,8,9],[7,8,9],[7,8,10],[7,9,10],[8,9,10],[8,9,11],[8,10,11],[9,10,11],[9,10,12],[9,11,12],\
          [10,11,12],[10,11,13],[10,12,13],[11,12,13],[11,12,14],[11,13,14],[12,13,14]]) {
            println 'Four card straight flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key && \
                  card[1] in max_deuceless_suit_vals.sort()[1..3]) {
                    hold_hand.add(card)
                }
            }
        }
        //three of a kind
        else if (grouped_deuceless_ranks.max{ it.value }.value == 2) {
            println 'Three of a kind!' + '\7'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][0] == grouped_deuceless_ranks.max{ it.value }.key) {
                    hold_hand.add(card)
                }
            }
        }
        //flush
        else if (grouped_deuceless_suits.size() == 1 && grouped_deuceless_ranks.size() == 4) {
            println 'Flush!' + '\7'
            hold_hand = hand.collect()
        }
        //straight
        else if (grouped_deuceless_ranks.size() == 4 && grouped_deuceless_ranks in [['A':1,'3':1,'4':1,'5':1],\
          ['3':1,'4':1,'5':1,'6':1],['3':1,'5':1,'6':1,'7':1],['3':1,'4':1,'6':1,'7':1],['3':1,'4':1,'5':1,'7':1],\
          ['4':1,'5':1,'6':1,'7':1],['4':1,'6':1,'7':1,'8':1],['4':1,'5':1,'7':1,'8':1],['4':1,'5':1,'6':1,'8':1],\
          ['5':1,'6':1,'7':1,'8':1],['5':1,'7':1,'8':1,'9':1],['5':1,'6':1,'8':1,'9':1],['5':1,'6':1,'7':1,'9':1],\
          ['6':1,'7':1,'8':1,'9':1],['6':1,'8':1,'9':1,'0':1],['6':1,'7':1,'9':1,'0':1],['6':1,'7':1,'8':1,'0':1],\
          ['7':1,'8':1,'9':1,'0':1],['7':1,'9':1,'0':1,'J':1],['7':1,'8':1,'0':1,'J':1],['7':1,'8':1,'9':1,'J':1],\
          ['8':1,'9':1,'0':1,'J':1],['8':1,'0':1,'J':1,'Q':1],['8':1,'9':1,'J':1,'Q':1],['8':1,'9':1,'0':1,'Q':1],\
          ['9':1,'0':1,'J':1,'Q':1],['9':1,'J':1,'Q':1,'K':1],['9':1,'0':1,'Q':1,'K':1],['9':1,'0':1,'J':1,'K':1],\
          ['0':1,'J':1,'Q':1,'K':1],['0':1,'Q':1,'K':1,'A':1],['0':1,'J':1,'K':1,'A':1],['0':1,'J':1,'Q':1,'A':1],\
          ['J':1,'Q':1,'K':1,'A':1]]) {
            println 'Straight!' + '\7'
            hold_hand = hand.collect()
        }
        //four of a straight flush, one gap
        else if (grouped_deuceless_suits.max{ it.value }.value == 3 && max_deuceless_suit_vals.sort() in [[4,5,14],\
          [3,4,7],[3,6,7],[4,5,8],[4,7,8],[5,6,9],[5,8,9],[6,7,10],[6,9,10],[7,8,11],[7,10,11],[8,9,12],[8,11,12],\
          [9,10,13],[9,12,13]]) {
            println 'Four of a straight flush with one gap'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals) {
                    hold_hand.add(card)
                }
            }
        }
        //four of a straight flush, two gaps
        else if (grouped_deuceless_suits.max{ it.value }.value == 3 && max_deuceless_suit_vals.sort() in [[3,5,14],\
          [3,5,7],[4,6,8],[5,7,9],[6,8,10],[7,9,11],[9,11,13],[10,12,14]]) {
            println 'Four of a straight flush with two gaps'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals) {
                    hold_hand.add(card)
                }
            }
        }
        //A34 suited, A35 suited, or A45 suited
        else if (grouped_deuceless_suits.max{ it.value }.value >= 2 && (3 in max_deuceless_suit_vals && \
          14 in max_deuceless_suit_vals) || (4 in max_deuceless_suit_vals && 14 in max_deuceless_suit_vals) || \
          (3 in max_deuceless_suit_vals && 4 in max_deuceless_suit_vals)) {
            println 'Ace-Three-Four suited'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals && \
                  card[1] in [14,3,4]) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value >= 2 && (3 in max_deuceless_suit_vals && \
          14 in max_deuceless_suit_vals) || (5 in max_deuceless_suit_vals && 14 in max_deuceless_suit_vals) || \
          (3 in max_deuceless_suit_vals && 5 in max_deuceless_suit_vals)) {
            println 'Ace-Three-Five suited'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals && \
                  card[1] in [14,3,5]) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value >= 2 && (4 in max_deuceless_suit_vals && \
          14 in max_deuceless_suit_vals) || (5 in max_deuceless_suit_vals && 14 in max_deuceless_suit_vals) || \
          (4 in max_deuceless_suit_vals && 5 in max_deuceless_suit_vals)) {
            println 'Ace-Four-Five suited'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals && \
                  card[1] in [14,4,5]) {
                    hold_hand.add(card)
                }
            }
        }
        //three of a royal flush, no ace
        else if (grouped_deuceless_suits.max{ it.value }.value == 2 && (max_suits.size() == 1 &&
          max_deuceless_suit_vals.sort() in [[10,11],[10,12],[10,13],[11,12],[11,13],[12,13]]) || \
          (max_suits.size() > 1 && max_deuceless_suit_vals.sort() in [[10,11],[10,12],[10,13],[11,12],\
          [11,13],[12,13]])) {
            println 'Three of a royal flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[1] in max_deuceless_suit_vals) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 3 && (max_suits.size() == 1 &&
          max_deuceless_suit_vals.sort()[1..2] in [[10,11],[10,12],[10,13],[11,12],[11,13],[12,13]])) {
            println 'Three of a royal flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[1] in max_deuceless_suit_vals.sort()[1..2]) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 2 && max_suits.size() > 1 && \
          max_deuceless_suit_vals2.sort() in [[10,11],[10,12],[10,13],[11,12],[11,13],[12,13]]) {
            println 'Three of a royal flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[1] in max_deuceless_suit_vals2) {
                    hold_hand.add(card)
                }
            }
        }
        //three of a straight flush
        else if (grouped_deuceless_suits.max{ it.value }.value == 2 && (max_suits.size() == 1 && \
          max_deuceless_suit_vals.sort() in [[3,14],[4,14],[3,4],[3,5],[3,6],[3,7],[4,5],[4,6],[4,7],[4,8],[5,6],\
          [5,7],[5,8],[5,9],[6,7],[6,8],[6,9],[6,10],[7,8],[7,9],[7,10],[7,11],[8,9],[8,10],[8,11],[8,12],[9,10],\
          [9,11],[9,12],[9,13]])) {
            println 'Three of a straight flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 2 && (max_suits.size() > 1 && \
          max_deuceless_suit_vals.sort() in [[3,14],[4,14],[5,14,],[3,4],[3,5],[3,6],[3,7],[4,5],[4,6],[4,7],[4,8],\
          [5,6],[5,7],[5,8],[5,9],[6,7],[6,8],[6,9],[6,10],[7,8],[7,9],[7,10],[7,11],[8,9],[8,10],[8,11],[8,12],\
          [9,10],[9,11],[9,12],[9,13]])) {
            println 'Three of a straight flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] in max_suits && card[1] in max_deuceless_suit_vals) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 2 && (max_suits.size() > 1 && \
          max_deuceless_suit_vals2.sort() in [[3,14],[4,14],[5,14,],[3,4],[3,5],[3,6],[3,7],[4,5],[4,6],[4,7],[4,8],\
          [5,6],[5,7],[5,8],[5,9],[6,7],[6,8],[6,9],[6,10],[7,8],[7,9],[7,10],[7,11],[8,9],[8,10],[8,11],[8,12],\
          [9,10],[9,11],[9,12],[9,13]])) {
            println 'Three of a straight flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] in max_suits2 && card[1] in max_deuceless_suit_vals2) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 3 && max_suits.size() == 1 && \
          max_deuceless_suit_vals.sort()[0..1] in [[3,14],[4,14],[3,4],[3,5],[3,6],[3,7],[4,5],[4,6],[4,7],[4,8],\
          [5,6],[5,7],[5,8],[5,9],[6,7],[6,8],[6,9],[6,10],[7,8],[7,9],[7,10],[7,11],[8,9],[8,10],[8,11],[8,12],\
          [9,10],[9,11],[9,12],[9,13]]) {
            println 'Three of a straight flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key && \
                  card[1] in max_deuceless_suit_vals.sort()[0..1]) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 3 && max_suits.size() > 1 && \
          max_deuceless_suit_vals2.sort()[0..1] in [[3,14],[4,14],[3,4],[3,5],[3,6],[3,7],[4,5],[4,6],[4,7],[4,8],\
          [5,6],[5,7],[5,8],[5,9],[6,7],[6,8],[6,9],[6,10],[7,8],[7,9],[7,10],[7,11],[8,9],[8,10],[8,11],[8,12],\
          [9,10],[9,11],[9,12],[9,13]]) {
            println 'Three of a straight flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] in max_suits2 && card[1] in max_deuceless_suit_vals2.sort()[0..1]) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 3 && max_suits.size() == 1 && \
          max_deuceless_suit_vals.sort()[1..2] in [[3,14],[4,14],[3,4],[3,5],[3,6],[3,7],[4,5],[4,6],[4,7],[4,8],\
          [5,6],[5,7],[5,8],[5,9],[6,7],[6,8],[6,9],[6,10],[7,8],[7,9],[7,10],[7,11],[8,9],[8,10],[8,11],[8,12],\
          [9,10],[9,11],[9,12],[9,13]]) {
            println 'Three of a straight flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key && \
                  card[1] in max_deuceless_suit_vals.sort()[1..2]) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 3 && max_suits.size() > 1 && \
          max_deuceless_suit_vals2.sort()[1..2] in [[3,14],[4,14],[3,4],[3,5],[3,6],[3,7],[4,5],[4,6],[4,7],[4,8],\
          [5,6],[5,7],[5,8],[5,9],[6,7],[6,8],[6,9],[6,10],[7,8],[7,9],[7,10],[7,11],[8,9],[8,10],[8,11],[8,12],\
          [9,10],[9,11],[9,12],[9,13]]) {
            println 'Three of a straight flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[0][1] in max_suits2 && card[1] in max_deuceless_suit_vals2.sort()[1..2]) {
                    hold_hand.add(card)
                }
            }
        }
        //three of a royal flush with an ace
        else if (grouped_deuceless_suits.max{ it.value }.value == 2 && max_suits.size() == 1 \
          && max_deuceless_suit_vals.sort() in [[10,11],[10,12],[10,13],[10,14],[11,12],[11,13],[11,14],[12,13],\
          [12,14],[13,14]]) {
            println 'Three of a royal flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[1] in max_deuceless_suit_vals) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 2 && max_suits.size() > 1 \
          && max_deuceless_suit_vals.sort() in [[10,11],[10,12],[10,13],[10,14],[11,12],[11,13],[11,14],[12,13],\
          [12,14],[13,14]]) {
            println 'Three of a royal flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[1] in max_deuceless_suit_vals) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 2 && max_suits.size() > 1 \
          && max_deuceless_suit_vals2.sort() in [[10,11],[10,12],[10,13],[10,14],[11,12],[11,13],[11,14],[12,13],\
          [12,14],[13,14]]) {
            println 'Three of a royal flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[1] in max_deuceless_suit_vals2) {
                    hold_hand.add(card)
                }
            }
        }
        else if (grouped_deuceless_suits.max{ it.value }.value == 3 && max_suits.size() == 1 \
          && max_deuceless_suit_vals.sort()[1..2] in [[10,11],[10,12],[10,13],[10,14],[11,12],[11,13],[11,14],[12,13],\
          [12,14],[13,14]]) {
            println 'Three of a royal flush'
            hold_hand = wild_cards.collect()
            for (card in hand) {
                if (card[1] in max_deuceless_suit_vals.sort()[1..2]) {
                    hold_hand.add(card)
                }
            }
        }
        //deuce
        else {
            hold_hand = wild_cards.collect()
        }
    }
    return hold_hand
}

def no_deuces(List hand) {
    List hold_hand = []
    List ranks = []
    List suits = []
    List deuceless_ranks = []
    List deuceless_suits = []
    List high_cards = []
    List wild_cards = []
    List max_deuceless_suit_vals = []
    List deuceless_rank_vals = []
    for (card in hand) {
        ranks.add(card[0][0])
        suits.add(card[0][1])
        if (card[0][0] in ['J','Q','K','A']) {
            high_cards.add(card)
        }
        if (card[0][0] == '2') {
            wild_cards.add(card)
        }
        else {
            deuceless_ranks.add(card[0][0])
            deuceless_suits.add(card[0][1])
        }
        if (card[1] in deuceless_rank_vals || card[1] == 2) {
        }
        else {
            deuceless_rank_vals.add(card[1])
        }
    }
    Map grouped_ranks = ranks.countBy { it }
    Map grouped_suits = suits.countBy { it }
    Map grouped_deuceless_ranks = deuceless_ranks.countBy { it }
    Map grouped_deuceless_suits = deuceless_suits.countBy { it }
    for (card in hand) {
        if (card[0][1] == grouped_deuceless_suits.max{ it.value }.key) {
            if (card[1] in max_deuceless_suit_vals || card[1] == 2) {
            }
            else {
                max_deuceless_suit_vals.add(card[1])
            }
        }
    }
    //natural royal flush
    if (grouped_suits.size() == 1 && grouped_ranks == ['0':1,'J':1,'Q':1,'K':1,'A':1]) {
        println 'Natural Royal flush!' + '\7'
        hold_hand = hand.collect()
    }
    //four of a royal flush
    else if (grouped_deuceless_suits.max{ it.value }.value == 4 && max_deuceless_suit_vals.sort() in \
      [[10,11,12,13],[10,11,12,14],[10,11,13,14],[10,12,13,14],[11,12,13,14]]) {
        println 'Four of a royal flush'
        for (card in hand) {
            if (card[0][1] == grouped_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals) {
                hold_hand.add(card)
            }
        }
    }
    //straight flush
    else if (grouped_suits.size() == 1 && grouped_ranks in [['A':1,'2':1,'3':1,'4':1,'5':1],\
      ['2':1,'3':1,'4':1,'5':1,'6':1],['3':1,'4':1,'5':1,'6':1,'7':1],['4':1,'5':1,'6':1,'7':1,'8':1],\
      ['5':1,'6':1,'7':1,'8':1,'9':1],['6':1,'7':1,'8':1,'9':1,'0':1],['7':1,'8':1,'9':1,'0':1,'J':1],\
      ['8':1,'9':1,'0':1,'J':1,'Q':1],['9':1,'0':1,'J':1,'Q':1,'K':1],['0':1,'J':1,'Q':1,'K':1,'A':1]]) {
        println 'Straight Flush!' + '\7'
        hold_hand = hand.collect()
    }
    //four of a kind
    else if (grouped_ranks.size() == 2) {
        if (grouped_ranks.max{ it.value }.value == 4) {
            println 'Four of a kind!' + '\7'
            for (card in hand) {
                if (card[0][0] == grouped_ranks.max{ it.value }.key) {
                    hold_hand.add(card)
                }
            }
        }
        //full house
        else if (grouped_ranks.max{ it.value }.value == 3) {
            println 'Full house!' + '\7'
            hold_hand = hand.collect()
        }
    }
    //three of a kind
    else if (grouped_ranks.size() == 3 && grouped_ranks.max{ it.value }.value == 3) {
        println 'Three of a kind!' + '\7'
        for (card in hand) {
            if (card[0][0] == grouped_ranks.max{ it.value }.key) {
                hold_hand.add(card)
            }
        }
    }
    //flush
    else if (grouped_suits.size() == 1 && grouped_ranks.size() == 5) {
        println 'Flush!' + '\7'
        hold_hand = hand.collect()
    }
    //straight
    else if (grouped_ranks.size() == 5 && grouped_ranks in [['A':1,'2':1,'3':1,'4':1,'5':1],\
      ['2':1,'3':1,'4':1,'5':1,'6':1],['3':1,'4':1,'5':1,'6':1,'7':1],['4':1,'5':1,'6':1,'7':1,'8':1],\
      ['5':1,'6':1,'7':1,'8':1,'9':1],['6':1,'7':1,'8':1,'9':1,'0':1],['7':1,'8':1,'9':1,'0':1,'J':1],\
      ['8':1,'9':1,'0':1,'J':1,'Q':1],['9':1,'0':1,'J':1,'Q':1,'K':1],['0':1,'J':1,'Q':1,'K':1,'A':1]]) {
        println 'Straight!' + '\7'
        hold_hand = hand.collect()
    }
    //four of a straight flush
    else if (grouped_suits.max{ it.value }.value == 4 && max_deuceless_suit_vals.sort() in [[3,4,5,14],[3,4,5,6],\
      [3,4,5,7],[3,4,6,7],[3,5,6,7],[4,5,6,7],[4,5,6,8],[4,5,7,8],[4,6,7,8],[5,6,7,8],[5,6,7,9],[5,6,8,9],\
      [5,7,8,9],[6,7,8,9],[6,7,8,10],[6,7,9,10],[6,8,9,10],[7,8,9,10],[7,8,9,11],[7,8,10,11],[7,9,10,11],\
      [8,9,10,11],[8,9,10,12],[8,9,11,12],[8,10,11,12],[9,10,11,12],[9,10,11,13],[9,10,12,13],[9,11,12,13],\
      [10,11,12,13],[10,11,12,14],[10,11,13,14],[10,12,13,14],[11,12,13,14]]) {
        println 'Four of a straight flush'
        for (card in hand) {
            if (card[0][1] == grouped_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals) {
                hold_hand.add(card)
            }
        }
    }
    //three of a royal flush
    else if (grouped_suits.max{ it.value }.value == 3 && max_deuceless_suit_vals.sort() in \
      [[10,11,12],[10,11,13],[10,11,14],[10,12,13],[10,12,14],[10,13,14], \
      [11,12,13],[11,12,14],[11,13,14],[12,13,14]] || \
      grouped_suits.max{ it.value }.value == 4 && max_deuceless_suit_vals.sort()[0] == 10 || \
      grouped_suits.max{ it.value }.value == 4 && max_deuceless_suit_vals.sort()[1..3] in \
      [[10,11,12],[10,11,13],[10,11,14],[10,12,13],[10,12,14],[10,13,14], \
      [11,12,13],[11,12,14],[11,13,14],[12,13,14]]) {
        println 'Three of a royal flush'
        for (card in hand) {
            if (card[0][1] == grouped_suits.max{ it.value }.key && card[1] in [10, 11, 12, 13, 14]) {
                hold_hand.add(card)
            }
        }
    }
    //pair
    else if (grouped_ranks.size() >= 3 && grouped_ranks.max{ it.value }.value == 2) {
        println 'Pair'
        List pair_hand = []
        for (card in hand) {
            if (card[0][0] in grouped_ranks.findAll{ it.value == 2 }) {
                pair_hand.add(card)
            }
        }
        List high_pair = pair_hand.max{ it[1] }
        for (card in hand) {
            if (card[1] == high_pair[1]) {
                hold_hand.add(card)
            }
        }
    }
    //four of a flush
    else if (grouped_suits.max{ it.value }.value == 4) {
        println 'Four of a flush'
        for (card in hand) {
            if (card[0][1] == grouped_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals) {
                hold_hand.add(card)
            }
        }
    }
    //four of an open straight
    else if (deuceless_rank_vals.size() == 5 && deuceless_rank_vals.sort()[0..3] in [[3,4,5,6],[4,5,6,7],[5,6,7,8],\
      [6,7,8,9],[7,8,9,10],[8,9,10,11],[9,10,11,12],[10,11,12,13]]) {
        println 'Four of an open straight'
        hold_hand = wild_cards.collect()
        for (card in hand) {
            if (card[1] in deuceless_rank_vals.sort()[0..3]) {
                hold_hand.add(card)
            }
        }
    }
    else if (deuceless_rank_vals.size() == 5 && deuceless_rank_vals.sort()[1..4] in [[3,4,5,6],[4,5,6,7],[5,6,7,8],\
      [6,7,8,9],[7,8,9,10],[8,9,10,11],[9,10,11,12],[10,11,12,13]]) {
        println 'Four of an open straight'
        hold_hand = wild_cards.collect()
        for (card in hand) {
            if (card[1] in deuceless_rank_vals.sort()[1..4]) {
                hold_hand.add(card)
            }
        }
    }
    //three of a straight flush
    else if (grouped_suits.max{ it.value }.value == 3 && max_deuceless_suit_vals.sort() in [[3,4,14],[3,5,14],\
      [4,5,14],[3,4,5],[3,4,6],[3,4,7],[3,5,6],[3,5,7],[3,6,7],[4,5,6],[4,5,7],[4,5,8],[4,6,7],[4,6,8],[4,7,8],\
      [5,6,7],[5,6,8],[5,6,9],[5,7,8],[5,7,9],[5,8,9],[6,7,8],[6,7,9],[6,7,10],[6,8,9],[6,8,10],[6,9,10],[7,8,9],\
      [7,8,10],[7,8,11],[7,9,10],[7,9,11],[7,10,11],[8,9,10],[8,9,11],[8,9,12],[8,10,11],[8,10,12],[8,11,12],\
      [9,10,11],[9,10,12],[9,10,13],[9,11,12],[9,11,13],[9,12,13],[10,11,12],[10,11,13],[10,11,14],[10,12,13],\
      [10,12,14],[10,13,14],[11,12,13],[11,12,14],[11,13,14],[12,13,14]]) {
        println 'Three of a straight flush'
        for (card in hand) {
            if (card[0][1] == grouped_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals) {
                hold_hand.add(card)
            }
        }
    }
    //ten-queen suited or jack-queen suited
    else if (grouped_deuceless_suits.max{ it.value }.value == 2 && max_deuceless_suit_vals.sort() in [[10,12]] || \
      grouped_deuceless_suits.max{ it.value }.value == 3 && max_deuceless_suit_vals.sort()[1..2] in [[10,12]]) {
        println 'Ten-queen suited'
        for (card in hand) {
            if (card[0][1] == grouped_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals && \
                card[1] in [10,12]) {
                hold_hand.add(card)
            }
        }
    }
    else if (grouped_deuceless_suits.max{ it.value }.value == 2 && max_deuceless_suit_vals.sort() in [[11,12]] || \
      grouped_deuceless_suits.max{ it.value }.value == 3 && max_deuceless_suit_vals.sort()[1..2] in [[11,12]]) {
        println 'Jack-queen suited'
        for (card in hand) {
            if (card[0][1] == grouped_suits.max{ it.value }.key && card[1] in max_deuceless_suit_vals && \
                card[1] in [11,12]) {
                hold_hand.add(card)
            }
        }
    }
    //four of a straight
    else if (deuceless_rank_vals.size() == 5 && deuceless_rank_vals.sort()[0..3] in [[3,4,5,7],[3,4,6,7],[3,5,6,7],\
      [4,5,6,8],[4,5,7,8],[4,6,7,8],[5,6,7,9],[5,6,8,9],[5,7,8,9],[6,7,8,10],[6,7,9,10],[6,8,9,10],[7,8,9,11],\
      [7,8,10,11],[7,9,10,11],[8,9,10,12],[8,9,11,12],[8,10,11,12],[9,10,11,13],[9,10,12,13],[9,11,12,13],\
      [10,11,12,14],[10,11,13,14],[10,12,13,14],[11,12,13,14]]) {
        println 'Four of a straight'
        for (card in hand) {
            if (card[1] in deuceless_rank_vals.sort()[0..3]) {
                hold_hand.add(card)
            }
        }
    }
    else if (deuceless_rank_vals.size() == 5 && deuceless_rank_vals.sort()[1..4] in [[3,4,5,7],[3,4,6,7],[3,5,6,7],\
      [4,5,6,8],[4,5,7,8],[4,6,7,8],[5,6,7,9],[5,6,8,9],[5,7,8,9],[6,7,8,10],[6,7,9,10],[6,8,9,10],[7,8,9,11],\
      [7,8,10,11],[7,9,10,11],[8,9,10,12],[8,9,11,12],[8,10,11,12],[9,10,11,13],[9,10,12,13],[9,11,12,13],\
      [10,11,12,14],[10,11,13,14],[10,12,13,14],[11,12,13,14]]) {
        println 'Four of a straight'
        for (card in hand) {
            if (card[1] in deuceless_rank_vals.sort()[1..4]) {
                hold_hand.add(card)
            }
        }
    }
    else if (deuceless_rank_vals.size() == 5 && grouped_ranks in [['A':1,'3':1,'4':1,'5':1,'8':1],\
      ['A':1,'3':1,'4':1,'5':1,'9':1],['A':1,'0':1,'3':1,'4':1,'5':1],['A':1,'J':1,'3':1,'4':1,'5':1],\
      ['A':1,'Q':1,'3':1,'4':1,'5':1],['A':1,'K':1,'3':1,'4':1,'5':1],]) {
        correct_strategy = 'Four of a straight'
        for (card in hand) {
            if (card[1] in [3,4,5,14]) {
                hold_hand.add(card)
            }
        }
    }
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
        String use_hold_hand = System.console().readLine 'Press <Enter> to use suggested holds or enter D to select discards. '
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
            print 'Press <Enter> to use suggested holds or enter D to select discards. ' + '\7'
        }
    }
    return [hand, replay_hand]
}

def draw(List hand, List deck, Integer num_hands) {
    List results = []
    for (i = 0; i < num_hands; i++) {
        results[i] = [[],0,'']
        hand.each {
            results[i][0].add(it)
        }
    }
    List working_deck = deck.collect()
    for (i = 0; i < results.size(); i++) {
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
    for (i = 0; i < results.size(); i++) {
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
    if (payout >= 0) {
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
    List deuceless_ranks = []
    List deuceless_suits = []
    List wild_cards = []
    for (card in hand) {
        ranks.add(card[0][0])
        suits.add(card[0][1])
        if (card[0][0] =='2') {
            wild_cards.add(card)
        }
        else {
            deuceless_ranks.add(card[0][0])
            deuceless_suits.add(card[0][1])
        }
    }
    Map grouped_ranks = ranks.countBy { it }
    Map grouped_suits = suits.countBy { it }
    Map grouped_deuceless_ranks = deuceless_ranks.countBy { it }
    Map grouped_deuceless_suits = deuceless_suits.countBy { it }
    if (wild_cards.size() == 0) {
        if (grouped_suits.size() == 1) {
            //Natural Royal Flush
            if (grouped_ranks == ['0':1,'J':1,'Q':1,'K':1,'A':1]) {
                score = 'natural royal flush'
                pay = 800
            }
            //Straight Flush
            else if (grouped_ranks in [['A':1,'2':1,'3':1,'4':1,'5':1],['2':1,'3':1,'4':1,'5':1,'6':1],\
                  ['3':1,'4':1,'5':1,'6':1,'7':1],['4':1,'5':1,'6':1,'7':1,'8':1],['5':1,'6':1,'7':1,'8':1,'9':1],\
                  ['6':1,'7':1,'8':1,'9':1,'0':1],['7':1,'8':1,'9':1,'0':1,'J':1],['8':1,'9':1,'0':1,'J':1,'Q':1],\
                  ['9':1,'0':1,'J':1,'Q':1,'K':1]]) {
                score = 'straight flush'
                pay = 9
            }
            //Flush
            else {
                score = 'flush'
                pay = 2
            }
        }
        else {
            if (grouped_ranks.size() == 2) {
                //Four of a Kind
                if (grouped_ranks.max{ it.value }.value == 4) {
                    score = 'four of a kind'
                    pay = 5
                }
                //Full House
                else {
                    score = 'full house'
                    pay = 3
                }
            }
            else if (grouped_ranks.size() == 3) {
                //Three of a Kind
                if (grouped_ranks.max{ it.value }.value == 3) {
                    score = 'three of a kind'
                    pay = 1
                }
                else {
                    score = "loser"
                    pay  = 0
                }
            }
            else if (grouped_ranks.size() == 4) {
                //Three of a Kind
                if (grouped_ranks.max{ it.value }.value == 3) {
                    score = 'three of a kind'
                    pay = 1
                }
                else {
                    score = "loser"
                    pay  = 0
                }
            }
            else if (grouped_deuceless_ranks.size() == 5) {
                //Straight
                if (grouped_ranks in [['A':1,'2':1,'3':1,'4':1,'5':1],['2':1,'3':1,'4':1,'5':1,'6':1],\
                  ['3':1,'4':1,'5':1,'6':1,'7':1],['4':1,'5':1,'6':1,'7':1,'8':1],['5':1,'6':1,'7':1,'8':1,'9':1],\
                  ['6':1,'7':1,'8':1,'9':1,'0':1],['7':1,'8':1,'9':1,'0':1,'J':1],['8':1,'9':1,'0':1,'J':1,'Q':1],\
                  ['9':1,'0':1,'J':1,'Q':1,'K':1],['0':1,'J':1,'Q':1,'K':1,'A':1]]) {
                    score = 'straight'
                    pay = 2
                }
                else {
                    score = 'loser'
                    pay = 0
                }
            }
        }
    }
    else if (wild_cards.size() == 4) {
        score = 'four deuces'
        pay = 250
    }
    else {
        if (grouped_deuceless_suits.size() == 1) {
            //Royal Flush
            if ((wild_cards.size() == 1 && grouped_deuceless_ranks in [['0':1,'J':1,'Q':1,'A':1],\
              ['0':1,'J':1,'Q':1,'K':1],['0':1,'J':1,'K':1,'A':1],['0':1,'Q':1,'K':1,'A':1],\
              ['J':1,'Q':1,'K':1,'A':1]]) || \
              (wild_cards.size() == 2 && grouped_deuceless_ranks in [['J':1,'Q':1,'K':1],['Q':1,'K':1,'A':1],\
              ['O':1,'J':1,'A':1],['0':1,'J':1,'K':1],['0':1,'J':1,'Q':1],['0':1,'Q':1,'A':1],['0':1,'K':1,'A':1],\
              ['0':1,'Q':1,'K':1],['J':1,'Q':1,'A':1],['J':1,'K':1,'A':1]]) || \
              (wild_cards.size() == 3 && grouped_deuceless_ranks in [['J':1,'K':1],['J':1,'Q':1],['Q':1,'A':1],\
              ['0':1,'A':1],['K':1,'A':1],['Q':1,'K':1],['0':1,'J':1],['0':1,'K':1],['0':1,'Q':1],['J':1,'A':1]])) {
                score = 'royal flush'
                pay = 25
            }
            //Straight Flush
            else if ((wild_cards.size() == 1 && grouped_deuceless_ranks in [['A':1,'3':1,'4':1,'5':1],\
              ['3':1,'4':1,'5':1,'6':1],['3':1,'5':1,'6':1,'7':1],['3':1,'4':1,'6':1,'7':1],\
              ['3':1,'4':1,'5':1,'7':1],['4':1,'5':1,'6':1,'7':1],['4':1,'6':1,'7':1,'8':1],\
              ['4':1,'5':1,'7':1,'8':1],['4':1,'5':1,'6':1,'8':1],['5':1,'6':1,'7':1,'8':1],\
              ['5':1,'7':1,'8':1,'9':1],['5':1,'6':1,'8':1,'9':1],['5':1,'6':1,'7':1,'9':1],\
              ['6':1,'7':1,'8':1,'9':1],['6':1,'7':1,'8':1,'0':1],['7':1,'8':1,'9':1,'0':1],\
              ['6':1,'8':1,'9':1,'0':1],['6':1,'7':1,'9':1,'0':1],['7':1,'9':1,'0':1,'J':1],\
              ['7':1,'8':1,'0':1,'J':1],['8':1,'9':1,'0':1,'J':1],['7':1,'8':1,'9':1,'J':1],\
              ['8':1,'9':1,'0':1,'Q':1],['8':1,'9':1,'J':1,'Q':1],['8':1,'0':1,'J':1,'Q':1],\
              ['9':1,'0':1,'J':1,'Q':1],['0':1,'J':1,'Q':1,'K':1],['9':1,'0':1,'Q':1,'K':1],\
              ['9':1,'J':1,'Q':1,'K':1],['9':1,'0':1,'J':1,'K':1]]) || \
              (wild_cards.size() == 2 && grouped_deuceless_ranks in [['A':1,'3':1,'4':1],\
              ['A':1,'3':1,'5':1],['A':1,'4':1,'5':1],['3':1,'4':1,'5':1],['3':1,'4':1,'6':1],\
              ['3':1,'4':1,'7':1],['3':1,'5':1,'6':1],['3':1,'5':1,'7':1],['3':1,'6':1,'7':1],\
              ['4':1,'5':1,'6':1],['4':1,'5':1,'7':1],['4':1,'5':1,'8':1],['4':1,'6':1,'7':1],\
              ['4':1,'6':1,'8':1],['4':1,'7':1,'8':1],['5':1,'6':1,'7':1],['5':1,'6':1,'8':1],\
              ['5':1,'6':1,'9':1],['5':1,'7':1,'8':1],['5':1,'7':1,'9':1],['5':1,'8':1,'9':1],\
              ['6':1,'7':1,'8':1],['6':1,'7':1,'9':1],['6':1,'7':1,'0':1],['6':1,'8':1,'9':1],\
              ['6':1,'8':1,'0':1],['6':1,'9':1,'0':1],['7':1,'8':1,'9':1],['7':1,'8':1,'0':1],\
              ['7':1,'8':1,'J':1],['7':1,'9':1,'0':1],['7':1,'9':1,'J':1],['7':1,'0':1,'J':1],\
              ['8':1,'9':1,'0':1],['8':1,'9':1,'J':1],['8':1,'9':1,'Q':1],['8':1,'0':1,'J':1],\
              ['8':1,'0':1,'Q':1],['8':1,'J':1,'Q':1],['9':1,'0':1,'J':1],['9':1,'0':1,'Q':1],\
              ['9':1,'0':1,'K':1],['9':1,'J':1,'Q':1],['9':1,'J':1,'K':1],['9':1,'Q':1,'K':1],\
              ['0':1,'J':1,'Q':1],['0':1,'J':1,'K':1],['0':1,'J':1,'A':1],['0':1,'Q':1,'K':1],\
              ['0':1,'Q':1,'A':1],['0':1,'K':1,'A':1],['J':1,'Q':1,'K':1],['J':1,'Q':1,'A':1],\
              ['J':1,'K':1,'A':1]]) || \
              (wild_cards.size() == 3 && grouped_deuceless_ranks in [['4':1,'5':1],['A':1,'3':1],\
              ['A':1,'4':1],['A':1,'5':1],['3':1,'4':1],['3':1,'5':1],['4':1,'6':1],['5':1,'6':1],\
              ['3':1,'6':1],['6':1,'7':1],['4':1,'7':1],['5':1,'7':1],['3':1,'7':1],['6':1,'8':1],\
              ['4':1,'8':1],['7':1,'8':1],['5':1,'8':1],['8':1,'9':1],['6':1,'9':1],['7':1,'9':1],\
              ['5':1,'9':1],['9':1,'0':1],['7':1,'0':1],['8':1,'0':1],['6':1,'0':1],['8':1,'J':1],\
              ['0':1,'J':1],['9':1,'J':1],['7':1,'J':1],['J':1,'Q':1],['8':1,'Q':1],['0':1,'Q':1],\
              ['9':1,'Q':1],['J':1,'K':1],['Q':1,'K':1],['0':1,'K':1],['9':1,'K':1]])) {
                score = 'straight flush'
                pay = 9
            }
            else if (grouped_deuceless_ranks.size() == 2) {
                //Four of a Kind
                if (grouped_deuceless_ranks.max{ it.value }.value == 4-wild_cards.size()) {
                    score = 'four of a kind'
                    pay = 5
                }
            }
            //Flush
            else {
                score = 'flush'
                pay = 2
            }
        }
        else {
            if (grouped_deuceless_ranks.size() == 1) {
                //Five of a Kind
                if (grouped_deuceless_ranks.max{ it.value }.value == 5-wild_cards.size()) {
                    score = 'five of a kind'
                    pay = 15
                }
            }
            else if (grouped_deuceless_ranks.size() == 2) {
                //Four of a Kind
                if (grouped_deuceless_ranks.max{ it.value }.value == 4-wild_cards.size()) {
                    score = 'four of a kind'
                    pay = 5
                }
                //Full House
                else {
                    score = 'full house'
                    pay = 3
                }
            }
            else if (grouped_deuceless_ranks.size() == 5-wild_cards.size()) {
                //Straight
                if ((wild_cards.size() == 1 && grouped_deuceless_ranks in [['A':1,'3':1,'4':1,'5':1],\
                  ['3':1,'4':1,'5':1,'6':1],['3':1,'5':1,'6':1,'7':1],['3':1,'4':1,'6':1,'7':1],\
                  ['3':1,'4':1,'5':1,'7':1],['4':1,'5':1,'6':1,'7':1],['4':1,'6':1,'7':1,'8':1],\
                  ['4':1,'5':1,'7':1,'8':1],['4':1,'5':1,'6':1,'8':1],['5':1,'6':1,'7':1,'8':1],\
                  ['5':1,'7':1,'8':1,'9':1],['5':1,'6':1,'8':1,'9':1],['5':1,'6':1,'7':1,'9':1],\
                  ['6':1,'7':1,'8':1,'9':1],['6':1,'7':1,'8':1,'0':1],['7':1,'8':1,'9':1,'0':1],\
                  ['6':1,'8':1,'9':1,'0':1],['6':1,'7':1,'9':1,'0':1],['7':1,'9':1,'0':1,'J':1],\
                  ['7':1,'8':1,'0':1,'J':1],['8':1,'9':1,'0':1,'J':1],['7':1,'8':1,'9':1,'J':1],\
                  ['8':1,'9':1,'0':1,'Q':1],['8':1,'9':1,'J':1,'Q':1],['8':1,'0':1,'J':1,'Q':1],\
                  ['9':1,'0':1,'J':1,'Q':1],['0':1,'J':1,'Q':1,'K':1],['9':1,'0':1,'Q':1,'K':1],\
                  ['9':1,'J':1,'Q':1,'K':1],['9':1,'0':1,'J':1,'K':1],['0':1,'J':1,'Q':1,'A':1],\
                  ['0':1,'J':1,'K':1,'A':1],['0':1,'Q':1,'K':1,'A':1],['J':1,'Q':1,'K':1,'A':1]]) || \
                  (wild_cards.size() == 2 && grouped_deuceless_ranks in [['A':1,'3':1,'4':1],\
                  ['A':1,'3':1,'5':1],['3':1,'4':1,'5':1],['A':1,'4':1,'5':1],['3':1,'4':1,'6':1],\
                  ['3':1,'5':1,'6':1],['4':1,'5':1,'6':1],['3':1,'6':1,'7':1],['5':1,'6':1,'7':1],\
                  ['3':1,'4':1,'7':1],['4':1,'6':1,'7':1],['4':1,'5':1,'7':1],['3':1,'5':1,'7':1],\
                  ['4':1,'6':1,'8':1],['5':1,'6':1,'8':1],['5':1,'7':1,'8':1],['4':1,'7':1,'8':1],\
                  ['6':1,'7':1,'8':1],['4':1,'5':1,'8':1],['5':1,'8':1,'9':1],['7':1,'8':1,'9':1],\
                  ['5':1,'6':1,'9':1],['6':1,'8':1,'9':1],['6':1,'7':1,'9':1],['5':1,'7':1,'9':1],\
                  ['6':1,'8':1,'0':1],['7':1,'8':1,'0':1],['7':1,'9':1,'0':1],['6':1,'9':1,'0':1],\
                  ['8':1,'9':1,'0':1],['6':1,'7':1,'0':1],['7':1,'9':1,'J':1],['8':1,'9':1,'J':1],\
                  ['7':1,'8':1,'J':1],['8':1,'0':1,'J':1],['7':1,'0':1,'J':1],['9':1,'0':1,'J':1],\
                  ['8':1,'9':1,'Q':1],['0':1,'J':1,'Q':1],['9':1,'J':1,'Q':1],['8':1,'J':1,'Q':1],\
                  ['9':1,'0':1,'Q':1],['8':1,'0':1,'Q':1],['J':1,'Q':1,'K':1],['0':1,'J':1,'K':1],\
                  ['9':1,'J':1,'K':1],['0':1,'Q':1,'K':1],['9':1,'0':1,'K':1],['9':1,'Q':1,'K':1],\
                  ['Q':1,'K':1,'A':1],['0':1,'J':1,'A':1],['0':1,'Q':1,'A':1],['0':1,'K':1,'A':1],\
                  ['J':1,'Q':1,'A':1],['J':1,'K':1,'A':1]]) || \
                  (wild_cards.size() == 3 && grouped_deuceless_ranks in [['4':1,'5':1],['A':1,'3':1],\
                  ['A':1,'4':1],['A':1,'5':1],['3':1,'4':1],['3':1,'5':1],['4':1,'6':1],['5':1,'6':1],\
                  ['3':1,'6':1],['6':1,'7':1],['4':1,'7':1],['5':1,'7':1],['3':1,'7':1],['6':1,'8':1],\
                  ['4':1,'8':1],['7':1,'8':1],['5':1,'8':1],['8':1,'9':1],['6':1,'9':1],['7':1,'9':1],\
                  ['5':1,'9':1],['9':1,'0':1],['7':1,'0':1],['8':1,'0':1],['6':1,'0':1],['8':1,'J':1],\
                  ['0':1,'J':1],['9':1,'J':1],['7':1,'J':1],['J':1,'Q':1],['8':1,'Q':1],['0':1,'Q':1],\
                  ['9':1,'Q':1],['J':1,'K':1],['Q':1,'K':1],['0':1,'K':1],['9':1,'K':1],['Q':1,'A':1],\
                  ['0':1,'A':1],['K':1,'A':1],['J':1,'A':1]])) {
                    score = 'straight'
                    pay = 2
                }
                //Three of a Kind
                else if (grouped_deuceless_ranks.max{ it.value }.value == 3-wild_cards.size()) {
                    score = 'three of a kind'
                    pay = 1
                }
                else {
                    score = "loser"
                    pay  = 0
                }
            }
            else if (grouped_deuceless_ranks.max{ it.value }.value == 3-wild_cards.size()) {
                score = 'three of a kind'
                pay = 1
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
