angular.module('lfg.controllers', [])

    .controller('AppCtrl', function ($scope, $ionicModal, $timeout, $auth) {

        // With the new view caching in Ionic, Controllers are only called
        // when they are recreated or on app start, instead of every page change.
        // To listen for when this page is active (for example, to refresh data),
        // listen for the $ionicView.enter event:
        //$scope.$on('$ionicView.enter', function(e) {
        //});

        // Form data for the login modal
        //LOGIN
        $scope.loginData = {};
        $ionicModal.fromTemplateUrl('templates/login.html', {
            scope: $scope
        }).then(function (modal) {
            $scope.modalLogin = modal;
        });
        $scope.closeLogin = function () {
            $scope.modalLogin.hide();
        };
        $scope.login = function () {
            $auth.logIn();
            if (!$auth.isLoggedIn()) {
                $scope.modalLogin.show();
            }
        };
        $scope.doLogin = function () {
            console.log('Doing login', $scope.loginData);
            $auth.createToken($scope.loginData.login, $scope.loginData.password);
        };
        //REGISTER
        $scope.registerData = {};
        $ionicModal.fromTemplateUrl('templates/register.html', {
            scope: $scope
        }).then(function (modal) {
            $scope.modalRegister = modal;
        });
        $scope.closeRegister = function () {
            $scope.modalRegister.hide();
        };
        $scope.register = function () {
            $scope.modalRegister.show();
        };
        $scope.doRegister = function () {
            console.log('Doing register', $scope.registerData);
            $auth.register($scope.registerData.login, $scope.registerData.password, $scope.registerData.email);
        };

    })

    .controller('PlaylistsCtrl', function ($scope) {
        $scope.playlists = [
            {title: 'Reggae', id: 1},
            {title: 'Chill', id: 2},
            {title: 'Dubstep', id: 3},
            {title: 'Indie', id: 4},
            {title: 'Rap', id: 5},
            {title: 'Cowbell', id: 6}
        ];
    })

    .controller('PlaylistCtrl', function ($scope, $stateParams) {
    });
