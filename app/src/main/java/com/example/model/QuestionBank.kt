package com.example.model

object QuestionBank {

    val allChallenges: List<ScienceChallenge> = listOf(
        // === MÉCANIQUE ===
        ScienceChallenge(
            id = "meca_1",
            subject = Subject.MECANIQUE,
            title = "Énergie Cinétique & Vitesse",
            question = "Pour doubler la vitesse d'une voiture de course (de v à 2v), par quel facteur son énergie cinétique Ec est-elle multipliée ?",
            formulaSnippet = "Ec = 1/2 · m · v²",
            options = listOf("Multipliée par 2", "Multipliée par 4", "Multipliée par 8", "Inchangée"),
            correctIndex = 1,
            explanation = "Comme la vitesse est élevée au carré (v²), doubler la vitesse multiplie l'énergie cinétique par 2² = 4. La voiture stocke 4 fois plus d'énergie !",
            gameBonusEffect = "+60 km/h Nitro cinétique !"
        ),
        ScienceChallenge(
            id = "meca_2",
            subject = Subject.MECANIQUE,
            title = "Adhérence en Virage (Force Centripète)",
            question = "Dans un virage de rayon R, quelle condition évite le dérapage centrifuge pour une adhérence μ ?",
            formulaSnippet = "F_centrifuge = (m · v²) / R ≤ μ · m · g",
            options = listOf("v² ≤ μ · g · R", "v² ≥ μ · g · R", "R ≤ μ · g · v", "m ≥ R / g"),
            correctIndex = 0,
            explanation = "La force d'adhérence maximale F_max = μ · m · g doit équilibrer la force centrifuge (m · v² / R), ce qui donne v² ≤ μ · g · R.",
            gameBonusEffect = "Adhérence maximale dans la corde du virage !"
        ),
        ScienceChallenge(
            id = "meca_3",
            subject = Subject.MECANIQUE,
            title = "Décélération & Freinage d'Urgence",
            question = "Une voiture de 1000 kg freine avec une force de 6000 N. Quelle est sa décélération a ?",
            formulaSnippet = "Principe Fondamental : Σ F = m · a",
            options = listOf("3 m/s²", "6 m/s²", "10 m/s²", "60 m/s²"),
            correctIndex = 1,
            explanation = "D'après la 2ème loi de Newton, a = F / m = 6000 N / 1000 kg = 6 m/s².",
            gameBonusEffect = "Freinage parfait avant l'obstacle !"
        ),
        ScienceChallenge(
            id = "meca_4",
            subject = Subject.MECANIQUE,
            title = "Effet Magnus au Football",
            question = "Pourquoi un ballon brossé en rotation vers la gauche incurve-t-il sa trajectoire vers la gauche ?",
            formulaSnippet = "F_magnus ∝ ρ · r³ · (ω ∧ v)",
            options = listOf(
                "L'air va plus vite d'un côté, créant une dépression (Bernoulli)",
                "La gravité s'annule sur le flanc gauche",
                "Le ballon devient plus lourd à gauche",
                "La force centrifuge le pousse en ligne droite"
            ),
            correctIndex = 0,
            explanation = "La rotation entraîne une couche d'air : l'air s'écoule plus vite du côté où la rotation va dans le sens du vent, créant une dépression qui aspire le ballon (effet Magnus).",
            gameBonusEffect = "Frappe brossée contournant le mur !"
        ),
        ScienceChallenge(
            id = "meca_5",
            subject = Subject.MECANIQUE,
            title = "Rapport de Boîte & Couple Moteur",
            question = "Sur une forte accélération au départ, quel rapport de vitesse offre le couple aux roues le plus élevé ?",
            formulaSnippet = "Couple aux roues = C_moteur · r_boite · r_pont",
            options = listOf("1ère vitesse (fort rapport de réduction)", "6ème vitesse (rapport surmultiplié)", "Point mort", "Tous les rapports donnent le même couple"),
            correctIndex = 0,
            explanation = "La 1ère vitesse possède le rapport de réduction le plus grand, multipliant le couple moteur au détriment de la vitesse de pointe.",
            gameBonusEffect = "Départ canon et accélération foudroyante !"
        ),

        // === PROGRAMMATION ===
        ScienceChallenge(
            id = "prog_1",
            subject = Subject.PROGRAMMATION,
            title = "Condition Booléenne du Turbo",
            question = "Pour quelle combinaison de variables le turbo s'active-t-il avec ce code ?",
            codeSnippet = "val boost = (speed >= 80 && !hasObstacle) || nitroActive",
            options = listOf(
                "speed = 60, hasObstacle = false, nitroActive = false",
                "speed = 100, hasObstacle = false, nitroActive = false",
                "speed = 100, hasObstacle = true, nitroActive = false",
                "speed = 40, hasObstacle = true, nitroActive = false"
            ),
            correctIndex = 1,
            explanation = "Avec speed = 100 (>= 80) et !hasObstacle (true), la partie gauche du OU (||) est vraie, donc boost devient vrai !",
            gameBonusEffect = "Surpression Turbo déclenchée !"
        ),
        ScienceChallenge(
            id = "prog_2",
            subject = Subject.PROGRAMMATION,
            title = "Algorithme du Gardien de But",
            question = "Le gardien de but exécute ce script d'IA. Si vous tirez avec power = 70 et spin = 25, où plonge-t-il ?",
            codeSnippet = "if (spin > 20) {\n    plongeon = \"DROITE\"\n} else if (power > 80) {\n    plongeon = \"HAUT_CENTRE\"\n} else {\n    plongeon = \"GAUCHE\"\n}",
            options = listOf("À DROITE", "EN HAUT AU CENTRE", "À GAUCHE", "Il reste immobile"),
            correctIndex = 0,
            explanation = "La première condition (spin > 20) est évaluée en premier. Comme spin = 25 > 20, il plonge immédiatement à DROITE. Tirez à GAUCHE pour marquer !",
            gameBonusEffect = "Prise à contre-pied du gardien !"
        ),
        ScienceChallenge(
            id = "prog_3",
            subject = Subject.PROGRAMMATION,
            title = "Boucle d'Évitement & Indexation",
            question = "Combien d'obstacles seront détectés et esquivés par cette boucle ?",
            codeSnippet = "for (lane in 0 until 4) {\n    if (lane % 2 == 0) avoidObstacle(lane)\n}",
            options = listOf("2 obstacles (voies 0 et 2)", "4 obstacles", "1 obstacle", "3 obstacles"),
            correctIndex = 0,
            explanation = "La boucle parcourt 0, 1, 2, 3. Les entiers pairs (modulo 2 == 0) sont 0 et 2, donc l'action s'exécute 2 fois.",
            gameBonusEffect = "Trajectoire d'esquive calculée à la perfection !"
        ),
        ScienceChallenge(
            id = "prog_4",
            subject = Subject.PROGRAMMATION,
            title = "Complexité Algorithmique de Trajectoire",
            question = "Quel est l'algorithme le plus rapide pour chercher le meilleur angle de tir dans une table triée de 1024 angles ?",
            codeSnippet = "Algorithme : Recherche dichotomique (Binary Search)",
            options = listOf("O(log N) - Recherche dichotomique", "O(N²) - Parcours naïf", "O(N!) - Brute force", "O(2^N) - Récursion non mémorisée"),
            correctIndex = 0,
            explanation = "La recherche dichotomique divise l'intervalle par 2 à chaque étape, trouvant la solution en seulement log2(1024) = 10 opérations !",
            gameBonusEffect = "Calcul instantané de la ligne optimale !"
        ),
        ScienceChallenge(
            id = "prog_5",
            subject = Subject.PROGRAMMATION,
            title = "Calcul Binaire & Registres",
            question = "Sur l'ordinateur de bord, le capteur renvoie le masque binaire 0b1100 (12) combiné avec 0b0011 (3) par un OU logique (|). Quelle est la valeur décimale ?",
            codeSnippet = "val status = 0b1100 or 0b0011",
            options = listOf("15 (0b1111)", "0 (0b0000)", "7 (0b0111)", "12 (0b1100)"),
            correctIndex = 0,
            explanation = "Le OU bit à bit (OR) met à 1 tous les bits présents : 1100 OR 0011 = 1111 en binaire, soit 8 + 4 + 2 + 1 = 15.",
            gameBonusEffect = "Circuit électronique entièrement débloqué !"
        ),

        // === MATHÉMATIQUES ===
        ScienceChallenge(
            id = "math_1",
            subject = Subject.MATHS,
            title = "Angle Balistique Optimal",
            question = "Dans le vide (sans frottement de l'air), quel angle de tir θ par rapport au sol donne la portée horizontale maximale ?",
            formulaSnippet = "Portée X_max = (v0² · sin(2θ)) / g",
            options = listOf("30°", "45°", "60°", "90°"),
            correctIndex = 1,
            explanation = "La portée dépend de sin(2θ). Le sinus est maximal (égal à 1) quand 2θ = 90°, donc quand θ = 45°.",
            gameBonusEffect = "Portée maximale et tir ultra-lointain !"
        ),
        ScienceChallenge(
            id = "math_2",
            subject = Subject.MATHS,
            title = "Vecteur Vitesse & Norme",
            question = "Un tir part avec une vitesse horizontale vx = 24 m/s et une vitesse verticale vy = 7 m/s. Quelle est sa vitesse totale ||v|| ?",
            formulaSnippet = "||v|| = √(vx² + vy²)",
            options = listOf("25 m/s", "31 m/s", "17 m/s", "49 m/s"),
            correctIndex = 0,
            explanation = "||v|| = √(24² + 7²) = √(576 + 49) = √625 = 25 m/s (c'est un triplet pythagoricien classique : 7, 24, 25).",
            gameBonusEffect = "Frappe surpuissante flashée à 90 km/h !"
        ),
        ScienceChallenge(
            id = "math_3",
            subject = Subject.MATHS,
            title = "Dérivée : Vitesse et Accélération",
            question = "La position d'un kart est x(t) = 4t² + 3t + 2. Quelle est son accélération constante a(t) ?",
            formulaSnippet = "v(t) = x'(t)  |  a(t) = v'(t) = x''(t)",
            options = listOf("8 m/s²", "4 m/s²", "8t + 3 m/s²", "12 m/s²"),
            correctIndex = 0,
            explanation = "La dérivée première est la vitesse v(t) = 8t + 3. La dérivée seconde est l'accélération a(t) = 8 m/s².",
            gameBonusEffect = "Accélération linéaire calibrée !"
        ),
        ScienceChallenge(
            id = "math_4",
            subject = Subject.MATHS,
            title = "Trigonométrie & Pente de la Lucarne",
            question = "Le ballon est à 10 m du but et la lucarne est à 2.4 m de haut. Que vaut la tangente de l'angle d'élévation tan(θ) ?",
            formulaSnippet = "tan(θ) = Côté Opposé / Côté Adjacent",
            options = listOf("0.24", "2.4", "4.16", "0.75"),
            correctIndex = 0,
            explanation = "tan(θ) = Hauteur / Distance = 2.4 / 10 = 0.24 (correspond à un angle d'environ 13.5°).",
            gameBonusEffect = "Tir millimétré droit en pleine lucarne !"
        ),
        ScienceChallenge(
            id = "math_5",
            subject = Subject.MATHS,
            title = "Produit Scalaire & Orthogonalité",
            question = "Deux vecteurs trajectoires u et v sont perpendiculaires (angle 90°). Que vaut leur produit scalaire u · v ?",
            formulaSnippet = "u · v = ||u|| · ||v|| · cos(θ)",
            options = listOf("0", "1", "||u|| + ||v||", "-1"),
            correctIndex = 0,
            explanation = "Puisque cos(90°) = 0, le produit scalaire de deux vecteurs orthogonaux est toujours nul : u · v = 0.",
            gameBonusEffect = "Changement de cap à angle droit sans perte de grip !"
        )
    )

    fun getChallengesFor(subject: Subject): List<ScienceChallenge> {
        return if (subject == Subject.MIXTE) {
            allChallenges
        } else {
            allChallenges.filter { it.subject == subject }
        }
    }

    fun getRandomChallenge(subject: Subject): ScienceChallenge {
        val pool = getChallengesFor(subject)
        return pool.random()
    }
}
