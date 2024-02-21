https://gfscott.com/blog/merge-git-repos-and-keep-commit-history/

# On créé le répertoire de mono repo et on va dedans
mkdir monorepo
cd monorepo

# On clone les projets qu'on veut merger ensemble
➜ git cl git@github.com:chutney-testing/chutney.git
➜ git cl git@github.com:chutney-testing/chutney-testing.github.io.git
➜ git cl git@github.com:chutney-testing/chutney-idea-plugin.git
➜ git cl git@github.com:chutney-testing/chutney-kotlin-dsl.git

# On doit avoir la structure suivant dans ./monorepo
---
➜ ls
chutney  chutney-idea-plugin  chutney-kotlin-dsl  chutney-testing.github.io
---

# Dans chaque répertoire projet, il faut préparer la structure qu'on va vouloir dans ./monorepo
# Au moment du merge des projets ensemble, le contenu de chaque projet est mis ensemble dans ./monorepo
# Donc pour éviter les conflits et le fourre-tout, il faut rajouter un niveau de répertoire supplémentaire, qui va "remonter" sous ./monorepo
# Si on ne fait pas ça, c'est tout le contenu de chaque répertoire, donc le bordel...
➜ cd chutney
➜ mkdir chutney # on créé un répertoire qui va contenir tout le projet, c'est lui qui va remonter après sous /monorepo
# on déplace tout dans chutney, sauf le .git et on doit avoir la structure suivante :
---
➜ ls -al 
.git chutney
---

# on ajoute les modifs et on commit
➜ git add .
➜ git ci -m "chore(chutney): Enedis transfert - monorepo migration"

# On refait pareil pour tous les projets qu'on va vouloir merger ensemble
# Après on doit avoir une arborescence telle que :

└── monorepo 
    └── chutney
        └── .git
        └── chutney
            ├── README.md
            ├── ...
    └── chutney-kotlin-dsl
        └── .git   
        └── kotlin-dsl
            ├── README.md
            ├── ...
    └── chutney-testing.github.io
        └── .git   
        └── docs
            ├── README.md
            ├── ...
    └── chutney-idea-plugin
        └── .git   
        └── idea-plugin
            ├── README.md
            ├── ...

# Après le merge, les répertoires idea-plugin, docs, kotlin-dsl et chutney vont remonter sous /monorepo
# Pour l'instant, on a un path tel que ./monorepo/chutney/chutney, il faut donc renommer sinon on aura un conflit 
# On veut avoir ./monorepo/chutney_tmp/chutney

➜ mv chutney chutney_tmp # on renomme le répertoire pour éviter le conflit après

# Ensuite, on créé un projet git dans ./monorepo
git init

# Puis on ajoute chaque répertoire comme si c'était un remote
➜ git remote add -f chutney ./chutney_tmp 
➜ git remote add -f chutney-testing.github.io ./chutney-testing.github.io
➜ git remote add -f chutney-idea-plugin ./chutney-idea-plugin
➜ git remote add -f kotlin-dsl ./chutney-kotlin-dsl

# On a maintenant fetch les repo et on doit voir les branches distantes tel que
---
➜ git br -a
  remotes/chutney-idea-plugin/master
  remotes/chutney-testing.github.io/main
  remotes/chutney/master
  remotes/kotlin-dsl/master
---

# Maintenant on fait le merge
# A chaque merge, le no-commit permet de vérifier, puis on valide en faisant un commit soit même
➜ git merge kotlin-dsl/master --no-commit --allow-unrelated-histories 
➜ git merge chutney-idea-plugin/master --no-commit --allow-unrelated-histories 
➜ git merge chutney-testing.github.io/main --no-commit --allow-unrelated-histories 
➜ git merge chutney/master --no-commit --allow-unrelated-histories 

---
➜ git ls
chutney
docs
idea-plugin
kotlin-dsl
---

# Après on supprime les répertoires qui ont servis de "remote" distant pour préparer la structure
---
➜ git st
## main
?? chutney-idea-plugin/
?? chutney-kotlin-dsl/
?? chutney-testing.github.io/
?? chutney_tmp/
---

# On supprime tous les remotes
---
➜ git remote -v
chutney	./chutney_tmp (fetch)
chutney	./chutney_tmp (push)
chutney-idea-plugin	./chutney-idea-plugin (fetch)
chutney-idea-plugin	./chutney-idea-plugin (push)
chutney-testing.github.io	./chutney-testing.github.io (fetch)
chutney-testing.github.io	./chutney-testing.github.io (push)
kotlin-dsl	./chutney-kotlin-dsl (fetch)
kotlin-dsl	./chutney-kotlin-dsl (push)
---

# Puis on squash tous les commits de migration en un seul

---
➜ git l
N 69a0ae78 (HEAD -> main) chore(kdsl): Enedis transfert - monorepo migration <Mael Besson>
N 881c7ae1 chore(idea-plugin): Enedis transfert - monorepo migration <Mael Besson>
N c4e9119e chore(docs): Enedis transfert - monorepo migration <Mael Besson>
N d96b7bed (kotlin-dsl/master) chore(kdsl): Enedis transfert - monorepo migration <Mael Besson>
N b17469e1 (chutney-testing.github.io/main) chore(docs): Enedis transfert - monorepo migration <Mael Besson>
N bfc6413e (chutney-idea-plugin/master) chore(idea-plugin): Enedis transfert - monorepo migration <Mael Besson>
N 45d04a72 (chutney/master) chore(chutney): Enedis transfert - monorepo migration <Mael Besson>
---

