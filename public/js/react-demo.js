/**
 * Created by tolet on 1/18/16.
 */
var axis = [
    {"id": 1, "name": "Yaba"},
    {"id": 2, "name": "Alimosho"}

];

var areas = [
    {"id": 1, "name": "Igando", "pId": 2},
    {"id": 2, "name": "Ikotun", "pId": 2},

    {"id": 3, "name": "Akoka", "pId": 1},
    {"id": 4, "name": "Oyingbo", "pId": 1}

];

var SingleOption = React.createClass({
    render: function () {
        return <option value={this.props.id}>{this.props.name}</option>;
    }
});

var OptionList = React.createClass({
    render: function () {
        var rows = this.props.data.map(function (p) {
            return (<SingleOption id={p.id} name={p.name} key={p.id + p.name}/>);
        });
        return <optgroup label="--Select--">{rows}</optgroup>;
    }
});

var TriggerComponent = React.createClass({
    handleChange: function (e) {
        var id = e.target.value;
        this.props.onHandleRemoteChange(id);
    },
    render: function () {
        var self = this;
        return (
            <select className="form-control" id="trigger_component" onChange={this.handleChange}>
                <OptionList data={this.props.data}/>
            </select>
        );
    }
});

var TargetComponent = React.createClass({

    render: function () {
        return (
            <select className="form-control" id="target_component">
                <OptionList data={this.props.data}/>
            </select>
        );
    }
});

var TriggerApp = React.createClass({

    loadAreasByAxis: function (id) {
        /*$.ajax({
         url: this.props.url+id,
         dataType: 'json',
         contentType: 'application/json',
         cache: false,
         success: function (data) {
         alert("data is : " + data);
         this.setState({remoteData : data });
         }.bind(this),
         error: function (xhr, status, err) {
         console.error(this.props.url, status, err.toString());
         }.bind(this)
         });*/
        var data = areas.filter(function (p) {
            return p.pId == id;
        });

        this.setState({remoteData: data});

    },

    handleRemoteChange: function (id) {
        var arr = this.loadAreasByAxis(id);
        console.log("The array is: " + JSON.stringify(arr));

    },

    getInitialState: function () {
        return {remoteData: []};
    },
    render: function () {
        return (
            <div>
                <TriggerComponent data={this.props.data} onHandleRemoteChange={this.handleRemoteChange}/>
                <TargetComponent data={this.state.remoteData}/>
            </div>
        );
    }
});

React.renderComponent(<TriggerApp data={axis} url="http://localhost:8080/areas/getAreasByAxis/"  className="demo"/>, document.getElementById('demo'));
