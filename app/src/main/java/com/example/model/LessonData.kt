package com.example.model

data class LessonTopic(
    val id: String,
    val subject: Subject,
    val title: String,
    val subtitle: String,
    val keyPoints: List<String>,
    val formulas: List<Pair<String, String>>, // label to formula string
    val concreteGameApplication: String,
    val interactiveType: InteractiveLabType
)

enum class InteractiveLabType {
    FRICTION_STOPPING_DISTANCE, // Meca: adjust mu and speed to see braking distance d = v²/(2*mu*g)
    MAGNUS_EFFECT_CURVE,        // Meca/Foot: adjust spin and kick speed to see ball deviation
    BOOLEAN_LOGIC_CIRCUIT,      // Prog: toggle inputs A and B and operator to see output
    ALGO_SEARCH_SPEED,          // Prog: compare Linear vs Binary search steps
    TRIGONOMETRY_CIRCLE,        // Math: adjust angle theta to see cos, sin, tan
    BALLISTIC_TRAJECTORY        // Math/Foot: adjust angle theta and initial speed to see trajectory curve
}

object LessonRepository {
    val topics: List<LessonTopic> = listOf(
        // MÉCANIQUE
        LessonTopic(
            id = "meca_newton_energie",
            subject = Subject.MECANIQUE,
            title = "Lois de Newton & Énergie Cinétique",
            subtitle = "Le socle fondamental de la vitesse et de la puissance",
            keyPoints = listOf(
                "1ère Loi (Inertie) : Un corps conserve sa vitesse tant que la résultante des forces est nulle.",
                "2ème Loi (Principe Fondamental) : Σ F = m · a. Pour accélérer vite, il faut une force motrice élevée et une masse réduite.",
                "Énergie Cinétique : Ec = 1/2 · m · v². Si la vitesse double, l'énergie cinétique quadruple !"
            ),
            formulas = listOf(
                "PFD" to "Σ F = m · a",
                "Énergie Cinétique" to "Ec = 1/2 · m · v²",
                "Puissance Mécanique" to "P = F · v = C · ω"
            ),
            concreteGameApplication = "Dans le jeu de course, la puissance transmise par le moteur et la boîte détermine l'accélération 'a'.",
            interactiveType = InteractiveLabType.FRICTION_STOPPING_DISTANCE
        ),
        LessonTopic(
            id = "meca_frottement_virage",
            subject = Subject.MECANIQUE,
            title = "Frottement, Adhérence & Effet Magnus",
            subtitle = "Tenue de route sur asphalte et frappe brossée au foot",
            keyPoints = listOf(
                "L'adhérence des pneus repose sur le frottement statique (coefficient μ). Si la force centrifuge dépasse μ·m·g, la voiture glisse.",
                "Force centrifuge en virage : Fc = (m · v²) / R. Plus le virage est serré (R petit) et la vitesse v grande, plus le risque est élevé.",
                "Effet Magnus : Une balle en rotation entraîne une couche d'air. La différence de vitesse d'air crée une dépression selon Bernoulli, courbant la balle !"
            ),
            formulas = listOf(
                "Condition de non-dérapage" to "v² ≤ μ · g · R",
                "Distance d'arrêt" to "d = v² / (2 · μ · g)",
                "Force de Magnus" to "F_m ∝ r³ · (ω ∧ v)"
            ),
            concreteGameApplication = "Utilisé pour calculer la trajectoire courbée du ballon contournant le mur au football !",
            interactiveType = InteractiveLabType.MAGNUS_EFFECT_CURVE
        ),

        // PROGRAMMATION
        LessonTopic(
            id = "prog_conditions_booleens",
            subject = Subject.PROGRAMMATION,
            title = "Logique Booléenne & Prise de Décision",
            subtitle = "Contrôler le flux d'exécution et les conditions de pilotage",
            keyPoints = listOf(
                "Opérateurs logiques : && (ET), || (OU), ! (NON), ^ (XOR).",
                "Court-circuit : Dans A && B, si A est faux, B n'est pas évalué. Dans A || B, si A est vrai, B n'est pas évalué.",
                "Algorithme décisionnel : Arbres de conditions 'if / else if / else' pour réagir en temps réel aux capteurs."
            ),
            formulas = listOf(
                "Table ET (AND)" to "VRAI && VRAI = VRAI, sinon FAUX",
                "Table OU (OR)" to "FAUX || FAUX = FAUX, sinon VRAI",
                "Loi de De Morgan" to "!(A && B) == (!A || !B)"
            ),
            concreteGameApplication = "Permet de lire et déjouer l'algorithme de plongée du gardien de but en temps réel !",
            interactiveType = InteractiveLabType.BOOLEAN_LOGIC_CIRCUIT
        ),
        LessonTopic(
            id = "prog_boucles_complexite",
            subject = Subject.PROGRAMMATION,
            title = "Boucles, Tableaux & Complexité O(N)",
            subtitle = "Optimiser les calculs pour tourner à 60 images/seconde",
            keyPoints = listOf(
                "Boucle for : Itération bornée idéale pour parcourir les voies de la piste de course.",
                "Boucle while : Répétition tant qu'une condition reste vraie (ex: tant que réservoir de nitro > 0).",
                "Complexité : O(1) est instantané, O(log N) divise par 2 à chaque étape (dichotomie), O(N) parcourt tout, O(N²) devient vite trop lent."
            ),
            formulas = listOf(
                "Accès direct tableau" to "O(1)",
                "Recherche dichotomique" to "O(log N)",
                "Recherche linéaire" to "O(N)"
            ),
            concreteGameApplication = "Dans la course, le moteur analyse les obstacles avec un algorithme optimisé en O(1) par voie.",
            interactiveType = InteractiveLabType.ALGO_SEARCH_SPEED
        ),

        // MATHÉMATIQUES
        LessonTopic(
            id = "math_trigo_balistique",
            subject = Subject.MATHS,
            title = "Trigonométrie & Balistique Parabolique",
            subtitle = "Trajectoire parfaite du coup franc et coordonnées polaires",
            keyPoints = listOf(
                "Cercle trigonométrique : cos(θ) donne l'axe horizontal x, sin(θ) donne l'axe vertical y.",
                "Équation balistique : x(t) = v0·cos(θ)·t, et y(t) = v0·sin(θ)·t - 1/2·g·t².",
                "Angle optimal : Sans frottement d'air, l'angle de 45° offre la distance maximale (portée X = v0²·sin(2θ)/g)."
            ),
            formulas = listOf(
                "Portée maximale" to "X_max = (v0² · sin(2θ)) / g",
                "Sommet de parabole (H)" to "H_max = (v0² · sin²(θ)) / (2g)",
                "Lien trigo fondamental" to "cos²(θ) + sin²(θ) = 1"
            ),
            concreteGameApplication = "Modèle mathématique exact utilisé dans le moteur physique du jeu de football !",
            interactiveType = InteractiveLabType.BALLISTIC_TRAJECTORY
        ),
        LessonTopic(
            id = "math_vecteurs_derivees",
            subject = Subject.MATHS,
            title = "Vecteurs & Dérivées Cinématiques",
            subtitle = "Vitesse instantanée, direction et produit scalaire",
            keyPoints = listOf(
                "Norme d'un vecteur : ||v|| = √(vx² + vy²). C'est la vitesse réelle affichée au compteur.",
                "Produit scalaire u·v = ux·vx + uy·vy = ||u||·||v||·cos(θ). Si u·v = 0, les vecteurs sont perpendiculaires.",
                "Dérivée temporelle : v(t) = dx/dt, et l'accélération a(t) = dv/dt = d²x/dt²."
            ),
            formulas = listOf(
                "Norme euclidienne" to "||v|| = √(vx² + vy²)",
                "Produit scalaire" to "u · v = ||u|| · ||v|| · cos(θ)",
                "Vitesse instantanée" to "v(t) = x'(t)"
            ),
            concreteGameApplication = "Calcul du vecteur impulsion au moment de la frappe du ballon ou du freinage en chicane.",
            interactiveType = InteractiveLabType.TRIGONOMETRY_CIRCLE
        )
    )
}
