## How to create this workspace from scratch
Workspace structure follows the Polylith Architecture ideas. The Polylith tool makes it easy to create a workspace, add components, and test and validate the workspace. Here, you can find all the steps required to create this workspace from scratch.

###### Install Clojure
If you do not have Clojure installed on your machine already, you can install it through [Homebrew](https://brew.sh/) on MacOS by running:

```sh
brew install clojure/tools/clojure
```

Refer to [this page on clojure.org](https://clojure.org/guides/getting_started) for more options on installing.

###### Install Polylith Tool
Polylith provides a command line tool that you can install on your machine to enhance your development experience. You can install it through Homebrew on MacOS by running:

```sh
brew install polyfy/polylith/poly
```

For more installation options, please have a look at [Polylith tool documentation](https://polylith.gitbook.io/poly/install/install).

###### Create a workspace
- `` poly create workspace name:realworld-app top-ns:clojure.realworld ``
  - This will create your workspace under a directory named `` realworld-app ``
  - Your code will end up in a package named `` clojure.realworld ``
  - Inside your workspace, you'll find the structure as in the image below.
  - If you look inside these directories, you'll see that bases, components, and projects are empty.

![workspace](.media/how-to/01_workspace.png)

Once the workspace is created, navigate to the workspace directory: `` cd realworld-backend ``

###### Open workspace in IDE
You can open the workspace with your favorite IDE. It will look like the following at this stage if you open it with [Intellij IDEA](https://www.jetbrains.com/idea/) with [Cursive](https://cursive-ide.com) plugin:

![dev-project](.media/how-to/02_dev_project.png)

###### Create components
- `` poly create component name:article ``
- `` poly create component name:comment ``
- `` poly create component name:database ``
- `` poly create component name:env ``
- `` poly create component name:log ``
- `` poly create component name:profile ``
- `` poly create component name:spec ``
- `` poly create component name:tag ``
- `` poly create component name:user ``

These command above will create components under `` components `` directory.

![components](.media/how-to/03_components.png)

However, our components are not yet added to development project's `` deps.edn ``. In order to start working with them, you need to add them to `` deps.edn `` in the root directory as following:

![components-added](.media/how-to/04_components_added_to_development_project.png)

notice that we added the components both under the `:dev` and `:test` alias. Once you do this and load dev and test aliases, you can start working with your components.

###### Create base
- `` poly create base name:rest-api ``

This command will create a base named `` rest-api `` under bases directory. 
Same as components, you should add the base to the `deps.edn` file in the workspace root. It will look like this:

![base](.media/how-to/05_base.png)

###### Add code to components and the base
You can take the code from the [repository](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app) to populate the components and the base. 
You should also add the necessary dependencies to the `` deps.edn `` file in the workspace root.

Once your code is ready, you can move on to the next step to create a project.

###### Create a project
- `` poly create project name:realworld-backend ``

This command will create a new directory under `` projects/realworld-backend ``. If you look into that directory, you will see that there is only a single file, named `` deps.edn `` and it will look like this:

![empty-project](.media/how-to/06_empty_project.png)

This is where you will place the configuration for your project. In Polylith, a project is a configuration which includes a single base, a set of components and library dependencies. Since our project is a very simple one with one single artifact, we'll include our only base and all of our components in this configuration. After adding those, it will look like this:

![filled-project](.media/how-to/07_filled_project.png)

As you can notice, we also have some extra configuration necessarry for our specific project, such as, ring configuration and two special aliases (`` :aot `` and `` :uberjar ``) for creating aot compiled uberjar artifact.

At this stage, you should have a copy of this repository. 

###### Workspace info
```sh
poly info
```

This command will print out the information about the current workspace. You can find documentation about it in the [Polylith tool documentation](https://polylith.gitbook.io/poly/). It should print an output like this:

<img src=".media/how-to/08_workspace_info.png" width="60%">

Here the asterisk symbol points the changed components and bases since the last stable point.

###### Validating intergrity
In order to validate the integratiy of the Polylith workspace, run the following command:

```sh
poly check
```

This command should output `` OK `` as message if everything is okay. Otherwise, it will print out errors and warnings found in the workspace.

###### Running tests
```sh
poly test
```

This command will run all the tests since the last stable point. Since this is a newly created workspace, last stable point will be since the beginning.

###### Adding a stable point
Once you are ready with your changes and the check and test commands run without any issues, you can commit your changes to your git repository. After commiting, you can add a git tag with `` stable- `` prefix. This will tell Polylith to take that commit as the last stable point next time you run any Polylith commands.

```sh
git tag -f -a "stable-master" -m "Stable point"
```

After adding tag, you can run info command again and get an output similar to this:

![workspace-info-after-commit](.media/how-to/09_workspace_info_after_commit.png)

#### Sample REPL run configuration for Intellij IDEA with Cursive
Polylith works out-of-the-box with Intellij IDEA + Cursive setup. Here is how my REPL run configuration looks like:

![repl-config](.media/how-to/10_repl_config.png)

The only thing that is different from default Cursive REPL configuration is, I selected Run with Deps option and added two aliases (``dev,test``) that comes from the Polylith workspace `` deps.edn ``.
