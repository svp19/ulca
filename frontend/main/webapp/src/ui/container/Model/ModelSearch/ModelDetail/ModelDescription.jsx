import { withStyles } from '@material-ui/core/styles';
import DatasetStyle from '../../../../styles/Dataset';
import { useHistory, useParams } from 'react-router';
import {
    Grid,
    Link,
    Typography,
    Card,
    Box,
    CardMedia,
    CardContent
} from '@material-ui/core';
import ImageArray from '../../../../../utils/getModelIcons';
import moment from 'moment';

const ModelDescription = (props) => {
    const { classes, title, para, index } = props;
    const history = useHistory();
    
    const getPara = (para) => {
        if(typeof para !== "string") {
            return moment(para).format("MM/DD/YYYY");
        }

        return (para[0] !== undefined) ? para.replace(para[0], para[0].toUpperCase()) : ""
    }

    return (
        /* {/* <Typography variant="h6" className={classes.modelTitle}>{title}</Typography>
             {title !== "Source URL" || para === "NA" ?
                 <Typography style={{ fontSize: '20px', fontFamily: 'Roboto', textAlign: "justify" }} className={title !== "Version" && classes.modelPara}>{para}</Typography> :
                 <Typography style={{ marginTop: '15px' }}><Link style={{ color: "#3f51b5", fontSize: '20px', }} variant="body2" href={para}>
                     {para}</Link></Typography>} */
        <Card sx={{ display: 'flex' }} style={{ minHeight: '100px', maxHeight: '100px', backgroundColor: ImageArray[index].color }}>
            <Grid container >
                <Grid item xs={3} sm={3} md={3} lg={3} xl={3} style={{ display: 'flex', marginTop: "21px", justifyContent: 'center' }}>
                    <div className={classes.descCardIcon} style={{ color: ImageArray[index].iconColor, backgroundColor: ImageArray[index].color }}>
                        {ImageArray[index].imageUrl}
                    </div>
                </Grid>
                <Grid item xs={9} sm={9} md={9} lg={9} xl={9} style={{ display: 'flex', marginTop: "5px" }}  className={classes.modelCard}>
                    {/* <Box sx={{ display: 'flex', flexDirection: 'row' }}> */}
                    <CardContent>
                        <Typography component="div" variant="subtitle2" style={{ marginBottom: '0px' ,paddingLeft:"0px" }} className={classes.cardTitle}>
                            {title}
                        </Typography  >
                        {title !== 'Source URL' || para === "NA" ?
                            <Typography variant="body2" color="text.secondary" className={classes.modelPara} >
                                 {getPara(para)}
                            </Typography> :
                            <Typography style={{
                                overflowWrap: "anywhere",
                                display: "-webkit-box",
                                "-webkit-line-clamp": "2",
                                "-webkit-box-orient": "vertical",
                                overflow: "hidden"
                            }}>
                                <Link style={{ color: "#3f51b5", fontSize: '14px' }} variant="body2" href={para}>
                                    {para}
                                </Link>
                            </Typography>
                        }
                    </CardContent>
                    {/* </Box> */}
                </Grid>
            </Grid>
        </Card>
    )
}
export default withStyles(DatasetStyle)(ModelDescription);