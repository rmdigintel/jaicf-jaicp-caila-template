package com.justai.jaicf.template.scenario

import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.template.scenario.generateNumber
import com.justai.jaicf.template.scenario.countBullsAndCows

val mainScenario = Scenario {

    state("start") {
        activators {
            regex("/start")
            intent("LetsPlay")
            intent("Hello")
        }
        action {
            if (context.client["welcome"] == null) {
                context.client["welcome"] = true
                reactions.say("Привет! Сыграем в игру \"Быки и коровы\"? Чтобы узнать правила, пиши \"правила\".")
            } else {
                reactions.say("Хочешь снова сыграть в \"Быки и коровы\"? Если забыл правила, пиши \"правила\".")
            }
        }
    }

    state("rules") {
        activators {
            regex("правила")
        }
        action {
            reactions.run {
                say(
                    "Я задумываю <u>4-значное</u> число с <u>неповторяющимися</u> цифрами, твоя задача его угадать.\n" +
                            "            В ответ я скажу сколько быков (сколько цифр ты угадал вплоть до позиции) и коров (без совпадения с позицией).\n" +
                            "            Например: Я загадал число 3219, ты пробуешь отгадать и называешь 2310.\n" +
                            "            В результате это две <b>коровы</b> (две цифры: \"2\" и \"3\" — угаданы на неверных позициях) и один <b>бык</b> (одна цифра \"1\" угадана вплоть до позиции)." +
                            "            Начинаем?"
                )
            }
        }
    }

    state("yes") {
        activators {
            intent("Yes")
        }
        action {
            reactions.run {
                context.session["botNumber"] = generateNumber()
                say("Здорово! Когда надоест, просто скажи \"<b>стоп</b>\" или \"<b>хватит</b>\"!")
                say("Я загадал число, попробуй его угадать. Введи четырехзначное число.")
                go("/game")
            }
        }
    }

    state("game") {

        state("play") {
            activators {
                regex("\\d+")
            }
            action {
                val botNumber = context.session["botNumber"] as String
                val playerNumber = request.input
                val playerNumberUnique = (playerNumber.split("")).distinct()
                if (playerNumber.length != 4) {
                    reactions.say("Число должно быть <b>четырехзначным</b>.")
                    reactions.go("/game")
                //5 [,1,2,3,4]
                } else if (playerNumberUnique.size != 5) {
                    reactions.say("Цифры не должны повторяться.")
                    reactions.go("/game")
                } else {
                    // reactions.say(botNumber)
                    val (bulls, cows) = countBullsAndCows(botNumber, playerNumber)
                    reactions.say("Число быков: $bulls, число коров: $cows")
                    if (bulls == 4) {
                        reactions.say("Поздравляю! Ты угадал число! Хочешь сыграть еще раз?")
                    } else {
                        reactions.say("Ты пока не угадал число! Попытайся еще. Введи четырехзначное число.")
                    }
                }
            }
        }

        state("localNo") {
            activators {
                intent("No")
            }
            action {
                reactions.say("Спасибо за игру!")
                reactions.go("/bye")
            }
        }

        state("localCatchAll") {
            activators {
                catchAll()
            }
            action {
                reactions.say("Неверный формат введеного числа.")
                reactions.go("/game")
            }
        }
    }

    state("no") {
        activators {
            intent("No")
        }
        action {
            reactions.say("Если захочешь поиграть, пиши \"давай поиграем\"!")
        }
    }

    state("stop") {
        activators {
            intent("Stop")
        }
        action {
            reactions.say("Спасибо за игру!")
            reactions.go("/bye")
        }
    }

    state("bye") {
        activators {
            intent("Bye")
        }
        action {
            reactions.say("Пока! Приходи еще!")
        }
    }

    fallback {
        reactions.say("Я ничего не понял. Переформулируй свой запрос.")
    }
}
