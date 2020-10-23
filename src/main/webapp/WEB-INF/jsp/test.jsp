    <!DOCTYPE html>
    <%
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", -1);
    %>
    <html>

    <HEAD>
        <META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
        <META HTTP-EQUIV="Expires" CONTENT="-1">
        <title>Control de guita</title>
    </HEAD>
    <link href="css/Style.css" rel="stylesheet">
    <script
            src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.9/angular.min.js"></script>
    <body>
    <div ng-app="myApp" ng-controller="movsCtrl">

        <button class="collapsible">Cargar movimientos</button>
        <div class="content">
            <div>
                <label for="periodo">periodo:</label> <input ng-model="load.periodo"
                                                             placeholder="Text" type="text"/>
            </div>
            <div>
                <label for="visacompra">visa compra:</label> <input ng-model="load.visacompra"
                                                                 placeholder="Text" type="text"/>
            </div>
            <div>
                <label for="visacuota">visa cuota:</label> <input ng-model="load.visacuota"
                                                                placeholder="Text" type="text"/>
            </div>
            <div>
                <label for="mastercompra">master compra:</label> <input ng-model="load.mastercompra"
                                                                   placeholder="Text" type="text"/>
            </div>
            <div>
                <label for="mastercuota">master cuota:</label> <input ng-model="load.mastercuota"
                                                                  placeholder="Text" type="text"/>
            </div>
            <div>
                <input type="submit" value="Submit" id="button-1" ng-click="sendAJAX()"/>
            </div>
        </div>

        <button class="collapsible">Movimientos de Compras</button>
        <div class="content">
            <p>
                <input type="text" ng-model="fcom" placeholder="Filtro">
                <label>
                    <select ng-model="listForOrder" ng-init="listForOrder='monto'">
                        <option ng-repeat="q in orders" value="{{q.val}}" selected="{{q.default}}">{{q.des}}</option>
                    </select>
                </label>
                <input type="checkbox" ng-model="descCompra" ng-init="descCompra=true">
            </p>
            <table>
                <tr>
                    <th>ID</th>
                    <th>Fecha</th>
                    <th>Descripcion</th>
                    <th>Tipo</th>
                    <th>Comprobante</th>
                    <th>Origen</th>
                    <th>Monto {{ getTotalCompra() | currency}}</th>
                    <th>Dolar</th>
                </tr>
                <tr ng-repeat="x in movs | filter : {'descripcion' : fcom} | orderBy: listForOrder: descCompra">
                    <td>{{$index + 1}}</td>
                    <td>{{x.fecha | date:'shortDate'}}</td>
                    <td>{{x.descripcion}}</td>
                    <td>{{x.tipo}}</td>
                    <td>{{x.comprobante}}</td>
                    <td>{{x.origen}}</td>
                    <td>{{x.monto | currency}}</td>
                    <td>{{x.dolar | currency}}</td>
                </tr>
            </table>
        </div>

        <button class="collapsible">Movimientos de Cuotas</button>
        <div class="content">
            <p>
                <input type="text" ng-model="fcuo" placeholder="Filtro">
                <select ng-model="listForOrderC" ng-init="listForOrderC='monto'">
                    <option ng-repeat="q in ordersc" value="{{q.val}}">{{q.des}}</option>
                </select>
                <input type="checkbox" ng-model="descCuota" ng-init="descCuota=true">
            </p>
            <table>
                <tr>
                    <th>ID</th>
                    <th>Fecha</th>
                    <th>Descripcion</th>
                    <th>Comprobante</th>
                    <th>Origen</th>
                    <th>Proximo {{getTotalProximo() | currency}}</th>
                    <th>Resto cuotas</th>
                    <th>Total cuotas</th>
                    <th>Monto {{getTotalCuota() | currency}}</th>
                    <th>Dolar</th>
                    <th>Monto Total {{getTotalCuota2() | currency}}</th>
                </tr>
                <tr ng-repeat="x in cuota | filter : {'descripcion' : fcuo} | orderBy: listForOrderC: descCuota" ng-model="cuotas">
                    <td>{{$index + 1}}</td>
                    <td>{{x.fecha | date:'shortDate'}}</td>
                    <td>{{x.descripcion}}</td>
                    <td>{{x.comprobante}}</td>
                    <td>{{x.origen}}</td>
                    <td>{{x.monto/x.resto | currency}}</td>
                    <td>{{x.resto}}</td>
                    <td>{{x.total}}</td>
                    <td>{{x.monto | currency}}</td>
                    <td>{{x.dolar | currency}}</td>
                    <td>{{x.monto/x.resto*x.total | currency}}</td>
                </tr>
            </table>
        </div>

        <button class="collapsible">Proyeccion</button>
        <div class="content">
            <table>
                <tr>
                    <th>ID</th>
                    <th>Descripcion</th>
                    <th>Origen</th>
                    <th>Resto cuotas</th>
                    <th>Total cuotas</th>
                    <th>Resto</th>
                    <th>{{getMonth(1)}} {{getMonthAmount(1) | currency}}</th>
                    <th>{{getMonth(2)}} {{getMonthAmount(2) | currency}}</th>
                    <th>{{getMonth(3)}} {{getMonthAmount(3) | currency}}</th>
                    <th>{{getMonth(4)}} {{getMonthAmount(4) | currency}}</th>
                    <th>{{getMonth(5)}} {{getMonthAmount(5) | currency}}</th>
                    <th>{{getMonth(6)}} {{getMonthAmount(6) | currency}}</th>
                    <th>{{getMonth(7)}} {{getMonthAmount(7) | currency}}</th>
                    <th>{{getMonth(8)}} {{getMonthAmount(8) | currency}}</th>
                    <th>{{getMonth(9)}} {{getMonthAmount(9) | currency}}</th>
                </tr>
                <tr ng-repeat="x in cuota | filter : {'descripcion' : fcuo} | orderBy: listForOrderC: descCuota" ng-model="cuotas">
                    <td>{{$index + 1}}</td>
                    <td>{{x.descripcion}}</td>
                    <td>{{x.origen}}</td>
                    <td>{{x.resto}}</td>
                    <td>{{x.total}}</td>
                    <td>{{x.monto | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,1) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,2) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,3) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,4) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,5) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,6) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,7) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,8) | currency}}</td>
                    <td>{{showAmountMonth(x.monto,x.resto,9) | currency}}</td>
                </tr>
            </table>
        </div>
    </div>

    <script>
        angular.module('myApp', []).controller('movsCtrl',
            function ($scope, $http) {
                $scope.getTotalCompra = function () {
                    let total = 0;
                    if (!$scope.movs) return;
                    $scope.movs.filter(function (a) {
                        return !$scope.fcom || a.descripcion.toLowerCase().match($scope.fcom.toLowerCase())
                    }).forEach(function (a) {
                        total += a.monto;
                    });
                    return total;
                }

                $scope.getTotalCuota = function () {
                    let total = 0;
                    if (!$scope.cuota) return;
                    $scope.cuota.filter(function (a) {
                        return !$scope.fcom || a.descripcion.toLowerCase().match($scope.fcuo.toLowerCase())
                    }).forEach(function (a) {
                        total += a.monto;
                    });
                    return total;
                }

                $scope.getTotalCuota2 = function () {
                    let total = 0;
                    if (!$scope.cuota) return;
                    $scope.cuota.filter(function (a) {
                        return !$scope.fcom || a.descripcion.toLowerCase().match($scope.fcuo.toLowerCase())
                    }).forEach(function (a) {
                        total += a.monto / a.resto * a.total;
                    });
                    return total;
                }

                $scope.getTotalProximo = function () {
                    let total = 0;
                    if (!$scope.cuota) return;
                    $scope.cuota.filter(function (a) {
                        return !$scope.fcom || a.descripcion.toLowerCase().match($scope.fcuo.toLowerCase())
                    }).forEach(function (a) {
                        total += a.monto / a.resto;
                    });
                    return total;
                }

                $scope.showAmountMonth = function(monto, resto, columna){
                    if(resto<columna) return null;

                    return monto/resto;
                }

                $scope.sendAJAX = function () {
                    $http.post("movs/setPeriodo", $scope.load.periodo);
                    $http.post("movs/visa1", $scope.load.visacompra);
                    $http.post("movs/visa2", $scope.load.visacuota);
                    $http.post("movs/master1", $scope.load.mastercompra);
                    $http.post("movs/master2", $scope.load.mastercuota);

                    $http.get("movs/get").then(function (response) {
                                    $scope.movs = response.data.movimientos;
                                    $scope.cuota = response.data.cuotas;
                                });
                }

                meses = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];

                $scope.getMonth = function(plus){
                    d = new Date();
                    return meses[(d.getMonth()+plus)%12];
                }

                $scope.getMonthAmount = function(plus){
                    let total = 0;
                    if (!$scope.cuota) return;
                    $scope.cuota.filter(function (a) {
                        //return !$scope.fcom || a.descripcion.toLowerCase().match($scope.fcuo.toLowerCase())
                        return a.resto >= plus;
                    }).forEach(function (a) {
                        total += a.monto / a.resto;
                    });
                    return total;
                }

                $http.get("movs/get").then(function (response) {
                    $scope.movs = response.data.movimientos;
                    $scope.cuota = response.data.cuotas;
                });

                $scope.orders = [
                    {des: "Tipo", val: "tipo", default:" ng-selected=\"{{q.default}}\""}
                    , {des: "Monto", val: "monto", default:true}
                    , {des: "Dolar", val: "dolar", default:false}
                    , {des: "Fecha", val: "fecha", default:false}
                    , {des: "Descripcion", val: "descripcion", default:false}
                ];

                $scope.ordersc = [
                    {des: "Monto", val: "monto", default:"selected"}
                    , {des: "Dolar", val: "dolar", default:false}
                    , {des: "Fecha", val: "fecha", default:false}
                    , {des: "Total", val: "total", default:false}
                    , {des: "Resto", val: "resto", default:false}
                    , {des: "Descripcion", val: "descripcion", default:false}
                ];

            });


    </script>
    <script>
        var coll = document.getElementsByClassName("collapsible");
        var i;

        for (i = 0; i < coll.length; i++) {
            coll[i].addEventListener("click", function () {
                const a = document.getElementsByClassName("content");
                const content = this.nextElementSibling;
                const max = content.style.maxHeight;
                for (let i = 0; i < a.length; i++) {
                    a[i].style.maxHeight = null;
                    a[i].previousElementSibling.classList.remove("active");
                }
                if (max) {
                    content.style.maxHeight = null;
                } else {
                    this.classList.toggle("active");
                    content.style.maxHeight = content.scrollHeight + "px";
                }
            });
        }
    </script>
    </body>
    </html>
