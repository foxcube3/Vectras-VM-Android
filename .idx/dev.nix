# To learn more about how to use Nix to configure your environment
# see: https://firebase.google.com/docs/studio/customize-workspace
{ pkgs, ... }: {
  # Which nixpkgs channel to use.
  channel = "stable-24.05"; # or "unstable"

  # Use https://search.nixos.org/packages to find packages
  packages = [
    pkgs.gh
    pkgs.jdk21
    pkgs.gradle
    pkgs.android-tools
    # pkgs.go
    # pkgs.python311
    # pkgs.python311Packages.pip
    # pkgs.nodejs_20
    # pkgs.nodePackages.nodemon
  ];

  # Sets environment variables in the workspace
  env = {};
  idx = {
    # Search for the extensions you want on https://open-vsx.org/ and use "publisher.id"
    extensions = [
      "streetsidesoftware.code-spell-checker"
      "vscjava.vscode-java-debug"
      "vscjava.vscode-java-pack"
      "Pleiades.java-extension-pack-jdk"
      "vscjava.vscode-gradle"
      "oderwat.indent-rainbow"
      "redhat.java"
      "ritwickdey.LiveServer"
      "vscjava.vscode-maven"
      "vscjava.vscode-java-dependency"
      "mechatroner.rainbow-csv"
      "vscjava.vscode-spring-boot-dashboard"
      "vmware.vscode-boot-dev-pack"
      "vmware.vscode-spring-boot"
      "vscjava.vscode-spring-initializr"
      "vscjava.vscode-java-test"
      "Gruntfuggly.todo-tree"
      "shardulm94.trailing-spaces"
      "redhat.vscode-xml"
    ];

    # Enable previews
    previews = {
      enable = true;
      previews = {
        # web = {
        #   # Example: run "npm run dev" with PORT set to IDX's defined port for previews,
        #   # and show it in IDX's web preview panel
        #   command = ["npm" "run" "dev"];
        #   manager = "web";
        #   env = {
        #     # Environment variables to set for your server
        #     PORT = "$PORT";
        #   };
        # };
      };
    };

    # Workspace lifecycle hooks
    workspace = {
      # Runs when a workspace is first created
      onCreate = {
        # Example: install JS dependencies from NPM
        # npm-install = "npm install";
      };
      # Runs when the workspace is (re)started
      onStart = {
        # Example: start a background task to watch and re-build backend code
        # watch-backend = "npm run watch-backend";
      };
    };
  };
}
